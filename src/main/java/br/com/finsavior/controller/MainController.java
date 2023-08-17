package br.com.finsavior.controller;

import br.com.finsavior.model.dto.MainTableRegisterRequestDTO;
import br.com.finsavior.model.dto.MainTableRegisterResponseDTO;
import br.com.finsavior.service.MainTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("main")
public class MainController {

    @Autowired
    MainTableService service;

    @PostMapping("/bill-register")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<MainTableRegisterResponseDTO> billRegister(@RequestBody MainTableRegisterRequestDTO mainTableRegisterRequestDTO) {
        return service.billRegister(mainTableRegisterRequestDTO);
    }
}
