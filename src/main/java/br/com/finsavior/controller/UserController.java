package br.com.finsavior.controller;

import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import br.com.finsavior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.finsavior.model.dto.LoginRequestDTO;
import br.com.finsavior.service.LoginService;

@RestController
@RequestMapping("user")
public class UserController {
	
	@Autowired
	LoginService loginService;

    @Autowired
    UserService userService;
    
    @PostMapping("/login-auth")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        return loginService.login(loginRequest);
    }

    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteAccountAndData(@RequestBody DeleteAccountRequestDTO deleteAccountRequest) {
        return userService.deleteAccount(deleteAccountRequest);
    }
    
    @GetMapping("/teste-autorizacao")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String testeAutorizacao() {
    	return "Sucesso";
    }
}
