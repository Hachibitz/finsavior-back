package br.com.finsavior.producer;


import java.util.UUID;

import br.com.finsavior.model.dto.WebhookRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebhookProducer {

    private final String topicName;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public WebhookProducer(@Value("${webhook.request.topic.name}") String topicName, KafkaTemplate<String, String> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    private static void accept(SendResult<String, String> result, Throwable ex) {
        if (ex != null) {
            log.error("Falha no envio: {}", ex.getMessage());
        } else {
            log.info("Mensagem enviada com sucesso!");
        }
    }

    public void sendMessage(WebhookRequestDTO message) throws JsonProcessingException {
        String id = UUID.randomUUID().toString();
        String messageJson = objectMapper.writeValueAsString(message);
        log.info("Publishing message: {}", message);
        kafkaTemplate.send(topicName, id, messageJson).whenComplete(WebhookProducer::accept);
    }
}
