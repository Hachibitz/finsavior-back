package br.com.finsavior.controller;

import br.com.finsavior.model.dto.LoginRequestDTO;
import br.com.finsavior.model.dto.ResetPasswordDTO;
import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<GenericResponseDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        return authService.signUp(signUpRequestDTO);
    }

    @PostMapping("/login-auth")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO loginRequest, HttpServletRequest request, HttpServletResponse response) {
        return authService.login(loginRequest, request, response);
    }

    @PostMapping("/login-google")
    public ResponseEntity<Map<String, String>> loginWithGoogle(@RequestBody String idTokenString, HttpServletRequest request, HttpServletResponse response) {
        return authService.loginWithGoogle(idTokenString, request, response);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        return authService.validateToken(token);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(@RequestBody String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/password-recovery")
    @ResponseStatus(HttpStatus.OK)
    public void passwordRecovery(@RequestParam String email) {
        authService.passwordRecovery(email);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        authService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());
    }
}
