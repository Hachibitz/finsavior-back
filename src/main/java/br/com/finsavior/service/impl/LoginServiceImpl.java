package br.com.finsavior.service.impl;

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

    public LoginServiceImpl(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

	@Override
	public ResponseEntity<String> login(LoginRequest loginRequest){
		try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);
            
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Falha ao autenticar usuário", e);
        }
	}
}
