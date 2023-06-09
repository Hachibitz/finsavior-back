package br.com.finsavior.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.finsavior.model.LoginRequest;

@Service
public interface LoginService {
	
	ResponseEntity<String> login(LoginRequest loginRequest);
}
