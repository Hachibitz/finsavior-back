package br.com.finsavior.producer;


import java.util.UUID;

import br.com.finsavior.model.dto.WebhookRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebhookProducer {

    private final String topicName;
    private final KafkaTemplate<String, WebhookRequestDTO> kafkaTemplate;

    public WebhookProducer(@Value("${webhook.request.topic.name}") String topicName, KafkaTemplate<String, WebhookRequestDTO> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    private static void accept(SendResult<String, WebhookRequestDTO> result, Throwable ex) {
        if (ex != null) {
            log.error("Falha no envio: {}", ex.getMessage());
        } else {
            log.info("Mensagem enviada com sucesso!");
        }
    }

    public void sendMessage(WebhookRequestDTO message){
        String id = UUID.randomUUID().toString();
        kafkaTemplate.send(topicName, id, message).whenComplete(WebhookProducer::accept);
    }
}
