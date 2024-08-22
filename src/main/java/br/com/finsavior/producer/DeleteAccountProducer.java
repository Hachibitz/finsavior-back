package br.com.finsavior.producer;

import br.com.finsavior.grpc.user.DeleteAccountRequest;
import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class DeleteAccountProducer {

    private final String topicName;
    private final KafkaTemplate<String, DeleteAccountRequestDTO> kafkaTemplate;

    public DeleteAccountProducer(@Value("${delete.account.topic.name}") String topicName, KafkaTemplate<String, DeleteAccountRequestDTO> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    private static void accept(SendResult<String, DeleteAccountRequestDTO> result, Throwable ex) {
        if (ex != null) {
            log.error("Falha no envio: {}", ex.getMessage());
        } else {
            log.info("Mensagem enviada com sucesso!");
        }
    }

    public void sendMessage(DeleteAccountRequestDTO message){
        String id = UUID.randomUUID().toString();
        kafkaTemplate.send(topicName, id, message).whenComplete(DeleteAccountProducer::accept);
    }
}
