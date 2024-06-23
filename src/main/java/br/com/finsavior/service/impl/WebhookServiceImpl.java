package br.com.finsavior.service.impl;

import br.com.finsavior.exception.BusinessException;
import br.com.finsavior.exception.EventNotFound;
import br.com.finsavior.grpc.webhook.Name;
import br.com.finsavior.grpc.webhook.Resource;
import br.com.finsavior.grpc.webhook.Subscriber;
import br.com.finsavior.grpc.webhook.WebhookMessageRequestDTO;
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

    private final ExternalUserRepository externalUserRepository;
    private final UserService userService;
    private final ExternalUserMapper externalUserMapper;
    private final WebhookProducer webhookProducer;

    @Override
    public void processWebhook(WebhookRequestDTO webhookRequestDTO) {
        ExternalUserDTO externalUserdto;
        try {
            externalUserdto = externalUserMapper.toExternalUserDTO(
                    externalUserRepository.findBySubscriptionId(webhookRequestDTO.getResource().getId())
            );

            switch(webhookRequestDTO.getEvent_type()){
                case BILLING_SUBSCRIPTION_ACTIVATED -> activatedEvent(externalUserdto, webhookRequestDTO);
                case BILLING_SUBSCRIPTION_EXPIRED -> expiredEvent(externalUserdto);
                case BILLING_SUBSCRIPTION_CANCELLED -> cancelledEvent(externalUserdto);
                case BILLING_SUBSCRIPTION_PAYMENT_FAILED -> paymentFailedEvent(externalUserdto);
                case BILLING_SUBSCRIPTION_SUSPENDED -> suspendedEvent(externalUserdto);
                default -> throw new EventNotFound("Evento não mapeado!");

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> sendMessage(WebhookRequestDTO webhookRequestDTO) {
        try {
            WebhookMessageRequestDTO webhookMessageRequestDTO = WebhookMessageRequestDTO.newBuilder()
                    .setId(webhookRequestDTO.getId())
                    .setCreateTime(webhookRequestDTO.getCreateTime())
                    .setEventType(webhookRequestDTO.getEvent_type().toString())
                    .setResource(buildResource(webhookRequestDTO.getResource()))
                    .build();
            webhookProducer.sendMessage(webhookMessageRequestDTO);
        }catch (Exception e){
            log.error("Error while sending message for webhook event, error = {}", e.getMessage());
        }
        return  ResponseEntity.ok().build();
    }

    private void suspendedEvent(ExternalUserDTO externalUser) {
        try {
            downgradeUserPlan(externalUser);
        } catch (Exception e) {
            throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
        }
    }

    private void paymentFailedEvent(ExternalUserDTO externalUser) {
        try {
            downgradeUserPlan(externalUser);
        } catch (Exception e) {
            throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
        }
    }

    private void cancelledEvent(ExternalUserDTO externalUser) {
        try {
            downgradeUserPlan(externalUser);
        } catch (Exception e) {
            throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
        }
    }

    private void expiredEvent(ExternalUserDTO externalUser) {
        try {
            downgradeUserPlan(externalUser);
        } catch (Exception e) {
            throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
        }
    }

    private void activatedEvent(ExternalUserDTO externalUser, WebhookRequestDTO webhookRequestDTO) {
        try {
            upgradeUserPlan(externalUser, webhookRequestDTO);
        } catch (Exception e) {
            throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
        }
    }

    private void createdEvent(ExternalUserDTO externalUser, WebhookRequestDTO webhookRequestDTO) {
    }

    private void downgradeUserPlan(ExternalUserDTO externalUser) {
        if((Objects.equals(externalUser.getPlanId(), PlanType.PLUS.getPlanTypeId()) ||
                Objects.equals(externalUser.getPlanId(), PlanType.PREMIUM.getPlanTypeId()))){
            externalUser.setPlanId(PlanType.FREE.getPlanTypeId());
            userService.updateUserPlan(externalUser);
        }
    }

    private void upgradeUserPlan(ExternalUserDTO externalUser, WebhookRequestDTO webhookRequestDTO) {
        if(Objects.equals(externalUser.getPlanId(), PlanType.FREE.getPlanTypeId())){
            externalUser.setPlanId(
                    PlanType.fromValue(webhookRequestDTO.getResource().getPlanId()).getPlanTypeId());
            userService.updateUserPlan(externalUser);
        }
    }

    private Resource buildResource(ResourceDTO resourceDTO) {
        return Resource.newBuilder()
                .setId(resourceDTO.getId())
                .setQuantity(resourceDTO.getQuantity())
                .setSubscriber(buildSubscriber(resourceDTO.getSubscriber()))
                .setCreateTime(resourceDTO.getCreateTime())
                .setPlanId(resourceDTO.getPlanId())
                .build();
    }

    private Subscriber buildSubscriber(SubscriberDTO subscriberDTO) {
        return Subscriber.newBuilder()
                .setName(Name.newBuilder()
                        .setGivenName(subscriberDTO.getName().getGiven_name())
                        .setSurname(subscriberDTO.getName().getSurname())
                        .build())
                .setEmail(subscriberDTO.getEmail())
                .build();
    }
}
