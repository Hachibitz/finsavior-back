package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.user.SignUpRequest;
import br.com.finsavior.grpc.user.SignUpResponse;
import br.com.finsavior.grpc.user.UserServiceGrpc;
import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.SignUpResponseDTO;
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

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public UserServiceImpl() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6566)
                .usePlaintext()
                .build();

        userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public ResponseEntity<SignUpResponseDTO> SignUp(SignUpRequestDTO signUpRequestDTO) {
        SignUpRequest signUpRequest = SignUpRequest.newBuilder()
                .setEmail(signUpRequestDTO.getEmail())
                .setUsername(signUpRequestDTO.getUsername())
                .setFirstName(signUpRequestDTO.getFirstName())
                .setLastName(signUpRequestDTO.getLastName())
                .setPassword(signUpRequestDTO.getPassword())
                .build();

        try {
            SignUpResponse signUpResponse = userServiceBlockingStub.signUp(signUpRequest);
            SignUpResponseDTO response = new SignUpResponseDTO(HttpResponseStatus.CREATED.toString(), signUpResponse.getMessage());
            logger.info("Usuário registrado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            SignUpResponseDTO response = new SignUpResponseDTO(HttpResponseStatus.INTERNAL_SERVER_ERROR.toString(), "Falha no registro");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
