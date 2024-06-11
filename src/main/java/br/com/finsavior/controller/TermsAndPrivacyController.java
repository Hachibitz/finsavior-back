package br.com.finsavior.controller;

import br.com.finsavior.service.TermsAndPrivacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/terms-and-privacy")
public class TermsAndPrivacyController {

    @Autowired
    private TermsAndPrivacyService termsAndPrivacyService;

    @GetMapping("/get-terms")
    public ResponseEntity<String> getTerms() {
        return termsAndPrivacyService.getTerms();
    }

    @GetMapping("/get-privacy-policy")
    public ResponseEntity<String> getPrivacyPolicy() {
        return termsAndPrivacyService.getPrivacyPolicy();
    }
}
