package br.com.finsavior.controller;

import br.com.finsavior.model.dto.SignUpRequestDTO;
import br.com.finsavior.model.dto.SignUpResponseDTO;
import br.com.finsavior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class SignUpController {

    @Autowired
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        return userService.SignUp(signUpRequestDTO);
    }
}
