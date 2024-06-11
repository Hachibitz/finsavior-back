package br.com.finsavior.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface TermsAndPrivacyService {
    public ResponseEntity<String> getTerms();
    public ResponseEntity<String> getPrivacyPolicy();
}
