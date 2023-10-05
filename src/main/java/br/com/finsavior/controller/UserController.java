package br.com.finsavior.controller;

import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import br.com.finsavior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/delete-account")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteAccountAndData(@RequestBody DeleteAccountRequestDTO deleteAccountRequest) {
        return userService.deleteAccount(deleteAccountRequest);
    }
    
    @GetMapping("/teste-autorizacao")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String testeAutorizacao() {
    	return "Sucesso";
    }
}
