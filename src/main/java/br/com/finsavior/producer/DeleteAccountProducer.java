package br.com.finsavior.producer;

import br.com.finsavior.grpc.user.DeleteAccountRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeleteAccountProducer {

    private final String topicName;
    private final KafkaTemplate<String, DeleteAccountRequest> kafkaTemplate;

    public DeleteAccountProducer(@Value("${delete.account.topic.name}") String topicName, KafkaTemplate<String, DeleteAccountRequest> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    private static void accept(SendResult<String, DeleteAccountRequest> result, Throwable ex) {
        Logger logger = LoggerFactory.getLogger(DeleteAccountProducer.class);
        if (ex != null) {
            logger.error("Falha no envio: "+ex.getMessage());
            ex.printStackTrace();
        } else {
            logger.info("Mensagem enviada com sucesso!");
        }
    }

    public void sendMessage(DeleteAccountRequest message){
        String id = UUID.randomUUID().toString();
        kafkaTemplate.send(topicName, id, message).whenComplete(DeleteAccountProducer::accept);
    }
}
