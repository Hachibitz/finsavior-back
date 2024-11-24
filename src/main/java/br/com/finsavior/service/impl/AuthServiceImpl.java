package br.com.finsavior.service.impl;

import br.com.finsavior.exception.AuthTokenException;
import br.com.finsavior.exception.BusinessException;
import br.com.finsavior.exception.UserNotFoundException;
import br.com.finsavior.grpc.security.AuthServiceGrpc;
import br.com.finsavior.grpc.security.SignUpRequest;
import br.com.finsavior.grpc.security.SignUpResponse;
import br.com.finsavior.model.dto.LoginRequestDTO;
import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.model.entities.PasswordResetToken;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.repository.PasswordResetTokenRepository;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.security.TokenProvider;
import br.com.finsavior.security.UserSecurityDetails;
import br.com.finsavior.service.AuthService;
import br.com.finsavior.service.EmailService;
import br.com.finsavior.service.GoogleAuthService;
import br.com.finsavior.util.PasswordValidator;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final Environment environment;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final GoogleAuthService googleAuthService;
    private final UserSecurityDetails userSecurityDetails;

    private AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;

    private String FINSAVIOR_RESET_PASSWORD_URL;

    @PostConstruct
    public void initialize() {
        String securityServiceHost = environment.getProperty("finsavior.security.service.host");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(securityServiceHost, 6567)
                .usePlaintext()
                .build();

        authServiceBlockingStub = AuthServiceGrpc.newBlockingStub(channel);
        FINSAVIOR_RESET_PASSWORD_URL = environment.getProperty("finsavior.front.resetPasswordUrl");
    }

    @Override
    public ResponseEntity<Map<String, String>> login(LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response){
        String userLogin = loginRequest.getUserLogin();
        User user = userRepository.findByUsername(loginRequest.getUserLogin());
        if(user==null){
            user = userRepository.findByEmail(loginRequest.getUserLogin());
            userLogin = user.getUsername();
        }
        log.info("Autenticando usuário: "+ loginRequest.getUserLogin()+ "...");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin, loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(userLogin, loginRequest.isRememberMe());

            Cookie tokenCookie = new Cookie("accessToken", accessToken);
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

            setCookieProperties(
                    tokenCookie, refreshTokenCookie,
                    loginRequest.isRememberMe(), request.getServerName()
            );
            response.addCookie(tokenCookie);
            response.addCookie(refreshTokenCookie);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            log.info(
                    "c={}, m={}, msg={}",
                    this.getClass().getSimpleName(),
                    "login", "Autenticado com sucesso!"
            );
            return ResponseEntity.ok(tokens);
        } catch (AuthenticationException e) {
            log.error(
                    "c={}, m={}, msg={}",
                    this.getClass().getSimpleName(),
                    "login", e.getMessage()
            );
            throw new RuntimeException("Falha ao autenticar usuário", e);
        }
    }

    private void setCookieProperties(
            Cookie tokenCookie, Cookie refreshTokenCookie,
            Boolean isRemeberMe, String serverName
    ) {
        int fifteenMinutesInMs = 15*60;
        tokenCookie.setDomain(serverName);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(fifteenMinutesInMs);

        refreshTokenCookie.setDomain(serverName);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(fifteenMinutesInMs);

        if(isRemeberMe) {
            int thirtyDaysExpirationInMs = 43800 * 60;
            refreshTokenCookie.setMaxAge(thirtyDaysExpirationInMs);
        }
    }

    @Override
    public ResponseEntity<String> refreshToken(String refreshToken) {
        if (tokenProvider.validateToken(refreshToken)) {
            String username = tokenProvider.getUsernameFromToken(refreshToken);
            User user = userRepository.findByUsername(username);

            if (user == null || user.isDelFg() || !user.isEnabled()) {
                throw new AuthTokenException("Usuário inválido ou inativo.", HttpStatus.UNAUTHORIZED);
            }

            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());

            String newAccessToken = tokenProvider.generateToken(
                    new UsernamePasswordAuthenticationToken(username, null, authorities)
            );

            return ResponseEntity.ok(newAccessToken);
        }

        throw new AuthTokenException("Refresh token inválido ou expirado.", HttpStatus.UNAUTHORIZED);
    }


    @Override
    public ResponseEntity<Map<String, String>> loginWithGoogle(String idTokenString, HttpServletRequest request, HttpServletResponse response) {
        try {
            GoogleIdToken.Payload payload = googleAuthService.validateGoogleToken(idTokenString);

            String email = payload.getEmail();
            User user = userRepository.findByEmail(email);

            if (user == null) {
                throw new UserNotFoundException("Usuário não encontrado.");
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, userSecurityDetails.loadUserByUsername(user.getUsername()).getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(user.getUsername(), true);

            Cookie tokenCookie = new Cookie("accessToken", accessToken);
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            setCookieProperties(tokenCookie, refreshTokenCookie, true, request.getServerName());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            response.addCookie(tokenCookie);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            throw new AuthTokenException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Boolean> validateToken(String token) {
        log.info("Validando token: {}", token);
        if (token != null && tokenProvider.validateToken(token)){
            return ResponseEntity.ok().body(true);
        }
        throw new AuthTokenException("Token não validado!", HttpStatus.UNAUTHORIZED);
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
            log.info("class = AuthServiceImpl, method = signUp, message = Usuário registrado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            log.error("class = AuthServiceImpl, method = signUp, message = {}", e.getMessage());
            GenericResponseDTO response = new GenericResponseDTO(HttpResponseStatus.INTERNAL_SERVER_ERROR.toString(), "Falha no registro: "+e.getStatus().getDescription());
            throw e;
        }
    }

    @Override
    public void passwordRecovery(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BusinessException("Email não encontrado");
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        String resetUrl = FINSAVIOR_RESET_PASSWORD_URL + token;

        try {
            passwordResetTokenRepository.save(passwordResetToken);
            emailService.sendEmail(email, "Password Recovery", "Clique no link para redefinir sua senha: " + resetUrl);
        } catch (Exception e) {
            log.error("method: {}, message: {}, error: {}", "sendEmail", "Falha no envio do email", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Token inválido"));

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Token expirado");
        }

        if (!PasswordValidator.isValid(newPassword)) {
            throw new BusinessException("Senha não atende aos requisitos mínimos");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(newPassword);
        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);
    }

}
