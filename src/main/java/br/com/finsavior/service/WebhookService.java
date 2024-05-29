package br.com.finsavior.service;

import br.com.finsavior.model.dto.WebhookRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface WebhookService {

    ResponseEntity<?> processWebhook(WebhookRequestDTO webhookRequestDTO);
}
