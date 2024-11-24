package br.com.finsavior.service;

import br.com.finsavior.model.dto.LoginRequestDTO;
import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthService {
    public ResponseEntity<GenericResponseDTO> signUp(SignUpRequestDTO signUpRequestDTO);
    ResponseEntity<Map<String, String>> login(LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<Boolean> validateToken(String token);
    void passwordRecovery(String email);
    void resetPassword(String token, String newPassword);
    ResponseEntity<Map<String, String>> loginWithGoogle(String idTokenString, HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<String> refreshToken(String refreshToken);
}
