package br.com.finsavior.controller;

import br.com.finsavior.model.dto.BillRegisterRequestDTO;
import br.com.finsavior.model.dto.BillRegisterResponseDTO;
import br.com.finsavior.service.BillRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bills")
public class BillRegisterController {

    @Autowired
    BillRegisterService service;

    @PostMapping("/bill-register")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<BillRegisterResponseDTO> billRegister(@RequestBody BillRegisterRequestDTO billRegisterRequestDTO) {
        return service.billRegister(billRegisterRequestDTO);
    }
}
