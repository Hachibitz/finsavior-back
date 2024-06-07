package br.com.finsavior.producer;

import br.com.finsavior.grpc.user.WebhookMessageRequestDTO;
import br.com.finsavior.model.dto.WebhookRequestDTO;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebhookProducer {

    private final String topicName;
    private final KafkaTemplate<String, WebhookMessageRequestDTO> kafkaTemplate;

    public WebhookProducer(@Value("${webhook.request.topic.name}") String topicName, KafkaTemplate<String, WebhookMessageRequestDTO> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    private static void accept(SendResult<String, WebhookMessageRequestDTO> result, Throwable ex) {
        Logger logger = LoggerFactory.getLogger(DeleteAccountProducer.class);
        if (ex != null) {
            logger.error("Falha no envio: {}", ex.getMessage());
        } else {
            logger.info("Mensagem enviada com sucesso!");
        }
    }

    public void sendMessage(WebhookMessageRequestDTO message){
        String id = UUID.randomUUID().toString();
        kafkaTemplate.send(topicName, id, message).whenComplete(WebhookProducer::accept);
    }
}
