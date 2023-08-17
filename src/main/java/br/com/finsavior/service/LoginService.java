package br.com.finsavior.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.finsavior.model.dto.LoginRequestDTO;

@Service
public interface LoginService {
	
	ResponseEntity<String> login(LoginRequestDTO loginRequest);
}
