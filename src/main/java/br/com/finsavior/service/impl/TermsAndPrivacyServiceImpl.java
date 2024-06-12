package br.com.finsavior.service.impl;

import br.com.finsavior.model.entities.PrivacyPolicy;
import br.com.finsavior.model.entities.TermsAndConditions;
import br.com.finsavior.repository.PrivacyPolicyRepository;
import br.com.finsavior.repository.TermsAndConditionsRepository;
import br.com.finsavior.service.TermsAndPrivacyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermsAndPrivacyServiceImpl implements TermsAndPrivacyService {

    private final TermsAndConditionsRepository termsAndConditionsRepository;
    private final PrivacyPolicyRepository privacyPolicyRepository;

    @Override
    public ResponseEntity<String> getTerms() {
        try {
            TermsAndConditions terms = termsAndConditionsRepository.getCurrentTerm();
            return ResponseEntity.ok(terms.getContent());
        } catch (Exception e) {
            log.error("method: {}, message: {}, erro: {}", "getTerms", "Falha ao buscar os termos, tente novamente mais tarde. ", e.getMessage());
            throw new RuntimeException("Falha ao buscar os termos, tente novamente mais tarde. "+e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> getPrivacyPolicy() {
        try {
            PrivacyPolicy terms = privacyPolicyRepository.getCurrentPrivacyPolicy();
            return ResponseEntity.ok(terms.getContent());
        } catch (Exception e) {
            log.error("method: {}, message: {}, erro: {}", "getTerms", "Falha ao buscar a política de privacidade, tente novamente mais tarde. ", e.getMessage());
            throw new RuntimeException("Falha ao buscar a política de privacidade, tente novamente mais tarde. "+e.getMessage());
        }
    }
}
