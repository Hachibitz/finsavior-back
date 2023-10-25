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
    public ResponseEntity<BillRegisterResponseDTO> billRegister(@RequestBody BillRegisterRequestDTO billRegisterRequestDTO) {
        return service.billRegister(billRegisterRequestDTO);
    }

    @GetMapping("/load-main-table-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadMainTableData() {
        return service.loadMainTableData();
    }

    @GetMapping("/load-card-table-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadCardTableData() {
        return service.loadCardTableData();
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
}
