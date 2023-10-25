package br.com.finsavior.service;

import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public ResponseEntity<?> deleteAccount(DeleteAccountRequestDTO deleteAccountRequestDTO);
}
