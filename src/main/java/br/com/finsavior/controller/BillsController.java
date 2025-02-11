package br.com.finsavior.controller;

import br.com.finsavior.model.dto.BillRegisterRequestDTO;
import br.com.finsavior.model.dto.BillRegisterResponseDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.service.BillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("bills")
public class BillsController {

    @Autowired
    BillsService service;

    @PostMapping("/bill-register")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BillRegisterResponseDTO> billRegister(@RequestBody BillRegisterRequestDTO billRegisterRequestDTO, @RequestParam boolean isRecurrent) {
        return service.billRegister(billRegisterRequestDTO, isRecurrent);
    }

    @GetMapping("/load-main-table-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadMainTableData(@RequestParam String billDate) {
        return service.loadMainTableData(billDate);
    }

    @GetMapping("/load-card-table-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadCardTableData(@RequestParam String billDate) {
        return service.loadCardTableData(billDate);
    }

    @DeleteMapping("/delete-item-table-main")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponseDTO> deleteItemFromMainTable(@RequestParam Long itemId) {
        return service.deleteItemFromMainTable(itemId);
    }

    @DeleteMapping("/delete-item-table-card")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponseDTO> deleteItemFromCardTable(@RequestParam Long itemId) {
        return service.deleteItemFromCardTable(itemId);
    }

    @PutMapping("/edit-item-table-card")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponseDTO> editItemFromCardTable(@RequestBody BillRegisterRequestDTO billRegisterRequestDTO) {
        return service.editItemFromCardTable(billRegisterRequestDTO);
    }

    @PutMapping("/edit-item-table-main")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponseDTO> editItemFromMainTable(@RequestBody BillRegisterRequestDTO billRegisterRequestDTO) {
        return service.editItemFromMainTable(billRegisterRequestDTO);
    }

    @PostMapping("/card-payment-register")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BillRegisterResponseDTO> cardPaymentRegister(@RequestBody BillRegisterRequestDTO billRegisterRequestDTO) {
        return service.cardPaymentRegister(billRegisterRequestDTO);
    }

    @GetMapping("/load-payment-card-table-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadPaymentCardTableData(@RequestParam String billDate) {
        return service.loadPaymentCardTableData(billDate);
    }
}
