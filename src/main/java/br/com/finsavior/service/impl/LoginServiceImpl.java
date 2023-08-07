package br.com.finsavior.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.finsavior.model.LoginRequest;
import br.com.finsavior.security.TokenProvider;
import br.com.finsavior.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService{
	
	private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    
    Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    public LoginServiceImpl(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

	@Override
	public ResponseEntity<String> login(LoginRequest loginRequest){
		logger.info("Autenticando usuário: "+ loginRequest.getUsername()+ "...");
		try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);
            
            logger.info("Autenticado com sucesso!");
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
        	logger.error("Falha ao autenticar usuário");
            throw new RuntimeException("Falha ao autenticar usuário", e);
        }
	}
}
