package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.security.AuthServiceGrpc;
import br.com.finsavior.grpc.security.SignUpRequest;
import br.com.finsavior.grpc.security.SignUpResponse;
import br.com.finsavior.model.dto.LoginRequestDTO;
import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.security.TokenProvider;
import br.com.finsavior.service.AuthService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final Environment environment;

    private AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;

    public AuthServiceImpl(AuthenticationManager authenticationManager, TokenProvider tokenProvider, Environment environment) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.environment = environment;
    }

    @PostConstruct
    public void initialize() {
        String securityServiceHost = environment.getProperty("finsavior.security.service.host");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(securityServiceHost, 6567)
                .usePlaintext()
                .build();

        authServiceBlockingStub = AuthServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public ResponseEntity<String> login(LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response){
        log.info("Autenticando usuário: "+ loginRequest.getUsername()+ "...");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication, loginRequest.isRememberMe());
            Cookie tokenCookie = new Cookie("token", token);

            tokenCookie.setDomain(request.getServerName());
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(120*60); //120 minutos
            if(loginRequest.isRememberMe()) {
                tokenCookie.setMaxAge(43800*60); //1 mês
            }
            response.addCookie(tokenCookie);

            log.info("Autenticado com sucesso!");
            return ResponseEntity.ok("Autenticado com sucesso!");
        } catch (AuthenticationException e) {
            log.error("Falha ao autenticar usuário");
            throw new RuntimeException("Falha ao autenticar usuário", e);
        }
    }

    @Override
    public ResponseEntity<Boolean> validateToken(String token) {
        log.info("Validando token: "+token);
        if (token != null && tokenProvider.validateToken(token)){
            return ResponseEntity.ok().body(true);
        }
        return ResponseEntity.ok().body(false);
    }

    @Override
    public ResponseEntity<GenericResponseDTO> signUp(SignUpRequestDTO signUpRequestDTO) {
        if(!signUpRequestDTO.isAgreement()) {
            GenericResponseDTO response = new GenericResponseDTO(HttpResponseStatus.INTERNAL_SERVER_ERROR.toString(), "Termos não aceitos.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        SignUpRequest signUpRequest = SignUpRequest.newBuilder()
                .setEmail(signUpRequestDTO.getEmail())
                .setUsername(signUpRequestDTO.getUsername())
                .setFirstName(signUpRequestDTO.getFirstName())
                .setLastName(signUpRequestDTO.getLastName())
                .setPassword(signUpRequestDTO.getPassword())
                .setPasswordConfirmation(signUpRequestDTO.getPasswordConfirmation())
                .build();

        try {
            SignUpResponse signUpResponse = authServiceBlockingStub.signUp(signUpRequest);
            GenericResponseDTO response = new GenericResponseDTO(HttpResponseStatus.CREATED.toString(), signUpResponse.getMessage());
            log.info("Usuário registrado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            log.error(e.getMessage());
            GenericResponseDTO response = new GenericResponseDTO(HttpResponseStatus.INTERNAL_SERVER_ERROR.toString(), "Falha no registro: "+e.getStatus().getDescription());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
