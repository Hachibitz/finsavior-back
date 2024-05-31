package br.com.finsavior.service;

import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.model.dto.SubscriptionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    public ResponseEntity<GenericResponseDTO> createSubscription(SubscriptionDTO subscription);
}
