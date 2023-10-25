package br.com.finsavior.service;

import br.com.finsavior.model.dto.BillRegisterRequestDTO;
import br.com.finsavior.model.dto.BillRegisterResponseDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BillsService {

    ResponseEntity<BillRegisterResponseDTO> billRegister(BillRegisterRequestDTO billRegisterRequestDTO);
    ResponseEntity<?> loadMainTableData();
    public ResponseEntity<?> loadCardTableData();
    public ResponseEntity<GenericResponseDTO> deleteItemFromMainTable(Long itemId);
    public ResponseEntity<GenericResponseDTO> deleteItemFromCardTable(Long itemId);
}
