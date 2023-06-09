package br.com.finsavior.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.finsavior.model.LoginRequest;
import br.com.finsavior.service.LoginService;

@RestController
public class UserController {
	
	@Autowired
	LoginService loginService;
    
    @PostMapping("/login-auth")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }
    
    @GetMapping("/teste-autorizacao")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String testeAutorizacao() {
    	return "Sucesso";
    }
}
