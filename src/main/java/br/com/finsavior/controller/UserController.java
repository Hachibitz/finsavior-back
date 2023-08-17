package br.com.finsavior.controller;

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
    
    @PostMapping("/login-auth")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        return loginService.login(loginRequest);
    }
    
    @GetMapping("/teste-autorizacao")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String testeAutorizacao() {
    	return "Sucesso";
    }
}
