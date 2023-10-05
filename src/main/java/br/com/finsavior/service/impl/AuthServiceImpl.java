package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.security.AuthServiceGrpc;
import br.com.finsavior.grpc.security.SignUpRequest;
import br.com.finsavior.grpc.security.SignUpResponse;
import br.com.finsavior.grpc.user.UserServiceGrpc;
import br.com.finsavior.model.dto.LoginRequestDTO;
import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.SignUpResponseDTO;
import br.com.finsavior.security.TokenProvider;
import br.com.finsavior.service.AuthService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    private final AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;

    public AuthServiceImpl(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6567)
                .usePlaintext()
                .build();

        authServiceBlockingStub = AuthServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public ResponseEntity<String> login(LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response){
        logger.info("Autenticando usuário: "+ loginRequest.getUsername()+ "...");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);
            Cookie tokenCookie = new Cookie("token", token);

            //tokenCookie.setDomain(request.getServerName());
            tokenCookie.setDomain(null);
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(3600);
            response.addCookie(tokenCookie);

            logger.info("Autenticado com sucesso!");
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            logger.error("Falha ao autenticar usuário");
            throw new RuntimeException("Falha ao autenticar usuário", e);
        }
    }

    @Override
    public ResponseEntity<Boolean> validateToken(String token) {
        logger.info("Validando token: "+token);
        if (token != null && tokenProvider.validateToken(token)){
            return ResponseEntity.ok().body(true);
        }
        return ResponseEntity.ok().body(false);
    }

    @Override
    public ResponseEntity<SignUpResponseDTO> signUp(SignUpRequestDTO signUpRequestDTO) {
        SignUpRequest signUpRequest = SignUpRequest.newBuilder()
                .setEmail(signUpRequestDTO.getEmail())
                .setUsername(signUpRequestDTO.getUsername())
                .setFirstName(signUpRequestDTO.getFirstName())
                .setLastName(signUpRequestDTO.getLastName())
                .setPassword(signUpRequestDTO.getPassword())
                .build();

        try {
            SignUpResponse signUpResponse = authServiceBlockingStub.signUp(signUpRequest);
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
