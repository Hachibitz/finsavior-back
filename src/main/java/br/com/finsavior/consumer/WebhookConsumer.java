package br.com.finsavior.consumer;

import br.com.finsavior.grpc.webhook.WebhookMessageRequestDTO;
import br.com.finsavior.model.mapper.ExternalUserMapper;
import br.com.finsavior.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookConsumer {

    private final WebhookService webhookService;
    private final ExternalUserMapper externalUserMapper;

    @KafkaListener(topics = {"${webhook.request.topic.name}"})
    public void onMessage(final WebhookMessageRequestDTO webhookMessageRequestDTO){
        try {
            webhookService.processWebhook(externalUserMapper.toWebhookRequestDTO(webhookMessageRequestDTO));
            log.info("Event for webhook request received successfully event: {}", webhookService);
        }catch (Exception e){
            log.error("Event failed to be consumed, event: {}, stackTrace: {}", webhookMessageRequestDTO, e.getStackTrace());
        }
    }
}
