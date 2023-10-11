package br.com.finsavior.service;

import br.com.finsavior.model.dto.BillRegisterRequestDTO;
import br.com.finsavior.model.dto.BillRegisterResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BillsService {

    ResponseEntity<BillRegisterResponseDTO> billRegister(BillRegisterRequestDTO billRegisterRequestDTO);
    ResponseEntity<?> loadMainTableData();
}
