package br.com.finsavior.service.impl;

import br.com.finsavior.exception.DeleteUserException;
import br.com.finsavior.exception.GenericException;
import br.com.finsavior.grpc.user.UserServiceGrpc;
import br.com.finsavior.grpc.user.UserServiceGrpc.UserServiceBlockingStub;
import br.com.finsavior.grpc.user.DeleteAccountRequest;
import br.com.finsavior.grpc.user.ChangePasswordRequest;
import br.com.finsavior.grpc.tables.GenericResponse;
import br.com.finsavior.model.dto.ChangePasswordRequestDTO;
import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.producer.DeleteAccountProducer;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.service.UserService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DeleteAccountProducer deleteAccountProducer;
    private final Environment environment;

    private UserServiceBlockingStub userServiceBlockingStub;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, DeleteAccountProducer deleteAccountProducer, Environment environment) {
        this.userRepository = userRepository;
        this.deleteAccountProducer = deleteAccountProducer;
        this.environment = environment;
    }

    @PostConstruct
    public void initialize() {
        String userServiceHost = environment.getProperty("finsavior.user.service.host");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(userServiceHost, 6566)
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
            GenericResponseDTO genericResponseDTO = new GenericResponseDTO(HttpStatus.OK.name(), "Conta adicionada na fila de exclusão com sucesso. Exclusão será processada nas próximas horas junto com todos os dados da conta.");
            deleteAccountProducer.sendMessage(message);
            log.info("Exclusão do usuário "+deleteAccountRequestDTO.getUsername()+" enviada para a fila com sucesso.");
            return ResponseEntity.ok(genericResponseDTO);
        } catch (StatusRuntimeException e) {
            log.error("Erro na exclusão, tente novamente em alguns minutos."+e.getStatus().getDescription());
            throw new DeleteUserException("Erro na exclusão: " + e.getStatus().getDescription());
        } catch (Exception e) {
            log.error("Erro na exclusão, tente novamente em alguns minutos."+e.getMessage());
            throw new DeleteUserException("Erro na exclusão, tente novamente em alguns minutos.");
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
            throw new GenericException("Erro ao realizar alteração de senha: " + e.getStatus().getDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
