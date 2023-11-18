package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.tables.GenericResponse;
import br.com.finsavior.grpc.user.*;
import br.com.finsavior.model.dto.ChangePasswordRequestDTO;
import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.producer.DeleteAccountProducer;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.service.UserService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    DeleteAccountProducer deleteAccountProducer;

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public UserServiceImpl() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6566)
                .usePlaintext()
                .build();

        userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public ResponseEntity<?> deleteAccount(DeleteAccountRequestDTO deleteAccountRequestDTO) {
        DeleteAccountRequest message = DeleteAccountRequest.newBuilder()
                .setUsername(deleteAccountRequestDTO.getUsername())
                .setPassword(deleteAccountRequestDTO.getPassword())
                .setConfirmation(deleteAccountRequestDTO.isConfirmation())
                .build();

        try {
            deleteAccountProducer.sendMessage(message);
            log.info("Exclusão do usuário "+deleteAccountRequestDTO.getUsername()+" enviada para a fila com sucesso.");
            return ResponseEntity.ok("Conta adicionada na fila de exclusão com sucesso. Exclusão será processada nas próximas horas junto com todos os dados da conta.");
        } catch (StatusRuntimeException e) {
            log.error("Erro na exclusão, tente novamente em alguns minutos."+e.getStatus().getDescription());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na exclusão: "+e.getStatus().getDescription());
        } catch (Exception e) {
            log.error("Erro na exclusão, tente novamente em alguns minutos."+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na exclusão, tente novamente em alguns minutos.");
        }
    }

    @Override
    public ResponseEntity<?> changeAccountPassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .setUsername(user.getUsername())
                .setCurrentPassword(changePasswordRequestDTO.getCurrentPassword())
                .setNewPassword(changePasswordRequestDTO.getNewPassword())
                .build();

        try {
            GenericResponse response = userServiceBlockingStub.changeAccountPassword(changePasswordRequest);
            return ResponseEntity.ok(response.getMessage());
        } catch (StatusRuntimeException e) {
            log.error("Erro ao realizar alteração de senha: {}", e.getStatus().getDescription());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao realizar alteração de senha: "+e.getStatus().getDescription());
        }
    }
}
