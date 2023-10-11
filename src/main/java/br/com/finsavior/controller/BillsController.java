package br.com.finsavior.controller;

import br.com.finsavior.model.dto.BillRegisterRequestDTO;
import br.com.finsavior.model.dto.BillRegisterResponseDTO;
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

    @GetMapping("/load-table-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> billRegister() {
        return service.loadMainTableData();
    }
}
