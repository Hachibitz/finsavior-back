package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.user.*;
import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.SignUpResponseDTO;
import br.com.finsavior.producer.DeleteAccountProducer;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.service.UserService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeleteAccountProducer deleteAccountProducer;

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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
                .setConfirmation(deleteAccountRequestDTO.isConfirmationAccepted())
                .build();

        try {
            deleteAccountProducer.sendMessage(message);
            logger.info("Exclusão do usuário "+deleteAccountRequestDTO.getUsername()+" enviada para a fila com sucesso.");
            return ResponseEntity.ok("Conta adicionada na fila de exclusão com sucesso. Exclusão será processada nas próximas horas junto com todos os dados da conta.");
        } catch (Exception e) {
            logger.error("Erro na exclusão, tente novamente em alguns minutos."+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na exclusão, tente novamente em alguns minutos.");
        }
    }
}
