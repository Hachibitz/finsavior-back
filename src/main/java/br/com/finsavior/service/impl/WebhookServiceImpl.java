package br.com.finsavior.service.impl;

import br.com.finsavior.exception.BusinessException;
import br.com.finsavior.exception.EventNotFound;
import br.com.finsavior.model.dto.ExternalUserDTO;
import br.com.finsavior.model.dto.ResourceDTO;
import br.com.finsavior.model.dto.SubscriberDTO;
import br.com.finsavior.model.dto.WebhookRequestDTO;
import br.com.finsavior.model.enums.PlanType;
import br.com.finsavior.model.mapper.ExternalUserMapper;
import br.com.finsavior.producer.WebhookProducer;
import br.com.finsavior.repository.ExternalUserRepository;
import br.com.finsavior.service.UserService;
import br.com.finsavior.service.WebhookService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final WebhookProducer webhookProducer;

    @Override
    public ResponseEntity<?> sendMessage(WebhookRequestDTO webhookRequestDTO) {
        try {
            webhookProducer.sendMessage(webhookRequestDTO);
        }catch (Exception e){
            log.error("Error while sending message for webhook event, error = {}", e.getMessage());
        }
        return  ResponseEntity.ok().build();
    }
}
