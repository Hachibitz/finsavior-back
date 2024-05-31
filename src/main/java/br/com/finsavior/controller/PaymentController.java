package br.com.finsavior.controller;

import br.com.finsavior.exception.BusinessException;
import br.com.finsavior.exception.PaymentException;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.model.dto.SubscriptionDTO;
import br.com.finsavior.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-subscription")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponseDTO> createSubscription(@RequestBody SubscriptionDTO subscription) {
        return paymentService.createSubscription(subscription);
    }
}
