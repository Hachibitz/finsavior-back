package br.com.finsavior.service;

import br.com.finsavior.model.dto.AiAdviceDTO;
import br.com.finsavior.model.dto.BillRegisterRequestDTO;
import br.com.finsavior.model.dto.BillRegisterResponseDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BillsService {

    ResponseEntity<BillRegisterResponseDTO> billRegister(BillRegisterRequestDTO billRegisterRequestDTO, boolean isRecurrent);
    ResponseEntity<?> loadMainTableData(String billDate);
    public ResponseEntity<?> loadCardTableData(String billDate);
    public ResponseEntity<GenericResponseDTO> deleteItemFromMainTable(Long itemId);
    public ResponseEntity<GenericResponseDTO> deleteItemFromCardTable(Long itemId);
    public ResponseEntity<GenericResponseDTO> editItemFromMainTable(BillRegisterRequestDTO billRegisterRequestDTO);
    public ResponseEntity<GenericResponseDTO> editItemFromCardTable(BillRegisterRequestDTO billRegisterRequestDTO);
    ResponseEntity<BillRegisterResponseDTO> cardPaymentRegister(BillRegisterRequestDTO billRegisterRequestDTO);
}
