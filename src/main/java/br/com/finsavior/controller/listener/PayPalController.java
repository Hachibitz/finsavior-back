package br.com.finsavior.controller.listener;

import br.com.finsavior.model.dto.WebhookRequestDTO;
import br.com.finsavior.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class PayPalController {

    private final WebhookService webhookService;

    @PostMapping("/subscription")
    public ResponseEntity<?> webhookListener(@RequestBody WebhookRequestDTO webhookRequestDTO){
        return webhookService.processWebhook(webhookRequestDTO);
    }
}
