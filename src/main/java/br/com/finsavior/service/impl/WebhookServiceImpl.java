package br.com.finsavior.service.impl;

import br.com.finsavior.exception.BusinessException;
import br.com.finsavior.exception.EventNotFound;
import br.com.finsavior.model.dto.ExternalUserDTO;
import br.com.finsavior.model.dto.WebhookRequestDTO;
import br.com.finsavior.model.enums.PlanType;
import br.com.finsavior.model.mapper.ExternalUserMapper;
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

    @Override
    public ResponseEntity<?> processWebhook(WebhookRequestDTO webhookRequestDTO) {
        ExternalUserDTO externalUserdto;
        try {
            externalUserdto = externalUserMapper.toExternalUserDTO(
                    externalUserRepository.findByServiceUserId(webhookRequestDTO.getResource().getId())
            );

            switch(webhookRequestDTO.getEvent_type()){
                case BILLING_SUBSCRIPTION_ACTIVATED -> activatedEvent(externalUserdto, webhookRequestDTO);
                case BILLING_SUBSCRIPTION_EXPIRED -> expiredEvent(externalUserdto, webhookRequestDTO);
                case BILLING_SUBSCRIPTION_CANCELLED -> cancelledEvent(externalUserdto, webhookRequestDTO);
                case BILLING_SUBSCRIPTION_PAYMENT_FAILED -> paymentFailedEvent(externalUserdto, webhookRequestDTO);
                case BILLING_SUBSCRIPTION_SUSPENDED -> suspendedEvent(externalUserdto, webhookRequestDTO);
                default -> throw new EventNotFound("Evento não mapeado!");

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().build();
    }

    private void suspendedEvent(ExternalUserDTO externalUser, WebhookRequestDTO webhookRequestDTO) {
        if((Objects.equals(externalUser.getPlanId(), PlanType.PLUS.getPlanTypeId()) ||
                Objects.equals(externalUser.getPlanId(), PlanType.PREMIUM.getPlanTypeId()))){
            externalUser.setPlanId(PlanType.FREE.getPlanTypeId());
            userService.updateUserPlan(externalUser);
        }
        throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
    }

    private void paymentFailedEvent(ExternalUserDTO externalUser, WebhookRequestDTO webhookRequestDTO) {
        if((Objects.equals(externalUser.getPlanId(), PlanType.PLUS.getPlanTypeId()) ||
                Objects.equals(externalUser.getPlanId(), PlanType.PREMIUM.getPlanTypeId()))){
            externalUser.setPlanId(PlanType.FREE.getPlanTypeId());
            userService.updateUserPlan(externalUser);
        }
        throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
    }

    private void cancelledEvent(ExternalUserDTO externalUser, WebhookRequestDTO webhookRequestDTO) {
        if((Objects.equals(externalUser.getPlanId(), PlanType.PLUS.getPlanTypeId()) ||
                Objects.equals(externalUser.getPlanId(), PlanType.PREMIUM.getPlanTypeId()))){
            externalUser.setPlanId(PlanType.FREE.getPlanTypeId());
            userService.updateUserPlan(externalUser);
        }
        throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
    }

    private void expiredEvent(ExternalUserDTO externalUser, WebhookRequestDTO webhookRequestDTO) {
        if((Objects.equals(externalUser.getPlanId(), PlanType.PLUS.getPlanTypeId()) ||
                Objects.equals(externalUser.getPlanId(), PlanType.PREMIUM.getPlanTypeId()))){
            externalUser.setPlanId(PlanType.FREE.getPlanTypeId());
            userService.updateUserPlan(externalUser);
        }
        throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
    }

    private void activatedEvent(ExternalUserDTO externalUser, WebhookRequestDTO webhookRequestDTO) {
        if(Objects.equals(externalUser.getPlanId(), PlanType.FREE.getPlanTypeId())){
            externalUser.setPlanId(
                    PlanType.fromValue(webhookRequestDTO.getResource().getPlanId()).getPlanTypeId());
            userService.updateUserPlan(externalUser);
        }
        throw new BusinessException("Error ao atualizar plano do usuário: " + externalUser.getUserId());
    }

    private void createdEvent(ExternalUserDTO externalUser, WebhookRequestDTO webhookRequestDTO) {
    }


}
