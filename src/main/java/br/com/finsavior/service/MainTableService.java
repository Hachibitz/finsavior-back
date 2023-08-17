package br.com.finsavior.service;

import br.com.finsavior.model.dto.MainTableRegisterRequestDTO;
import br.com.finsavior.model.dto.MainTableRegisterResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MainTableService {

    ResponseEntity<MainTableRegisterResponseDTO> billRegister(MainTableRegisterRequestDTO mainTableRegisterRequestDTO);
}
