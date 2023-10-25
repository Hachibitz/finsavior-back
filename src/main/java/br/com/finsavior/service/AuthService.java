package br.com.finsavior.service;

import br.com.finsavior.model.dto.LoginRequestDTO;
import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    public ResponseEntity<GenericResponseDTO> signUp(SignUpRequestDTO signUpRequestDTO);

    ResponseEntity<String> login(LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<Boolean> validateToken(String token);
}
