package br.com.finsavior.service.impl;

import br.com.finsavior.exception.PaymentException;
import br.com.finsavior.grpc.payment.PaymentServiceGrpc;
import br.com.finsavior.grpc.payment.PaymentServiceGrpc.PaymentServiceBlockingStub;
import br.com.finsavior.grpc.payment.SubscriptionRequest;
import br.com.finsavior.grpc.tables.GenericResponse;
import br.com.finsavior.grpc.tables.TableDataServiceGrpc;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.model.dto.SubscriptionDTO;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.model.enums.ExternalProvider;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.service.PaymentService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final Environment environment;
    private final UserRepository userRepository;

    private PaymentServiceBlockingStub paymentServiceBlockingStub;

    @PostConstruct
    public void initialize() {
        String tableServiceHost = environment.getProperty("finsavior.payment.service.host");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(tableServiceHost, 6568)
                .usePlaintext()
                .build();

        paymentServiceBlockingStub = PaymentServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public ResponseEntity<GenericResponseDTO> createSubscription(SubscriptionDTO subscription) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        SubscriptionRequest subscriptionRequest = SubscriptionRequest.newBuilder()
                .setUserId(user.getId())
                .setPlanId(subscription.getPlanId())
                .setSubscriptionId(subscription.getSubscriptionId())
                .setExternalProvider(ExternalProvider.PAYPAL.name())
                .setExternalUserId(subscription.getExternalUserId())
                .setExternalUserEmail(subscription.getPayer().getExternalEmailAddress())
                .build();

        try {
            GenericResponse grpcResponse = paymentServiceBlockingStub.createSubscription(subscriptionRequest);
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.OK.name(), grpcResponse.getMessage());
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            log.error("method: {}, message: {} {}", "createSubscription", "Falha ao criar a subscrição na base local: ", e.getStatus().getDescription());
            throw new PaymentException(e.getMessage());
        }
    }
}
