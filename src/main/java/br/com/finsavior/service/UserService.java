package br.com.finsavior.service;

import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.SignUpResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public ResponseEntity<SignUpResponseDTO> signUp(SignUpRequestDTO signUpRequestDTO);

    public ResponseEntity<?> deleteAccount(DeleteAccountRequestDTO deleteAccountRequestDTO);
}
