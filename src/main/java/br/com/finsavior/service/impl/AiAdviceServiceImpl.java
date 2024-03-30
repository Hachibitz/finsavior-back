package br.com.finsavior.service.impl;

import br.com.finsavior.grpc.tables.AiAdviceRequest;
import br.com.finsavior.grpc.tables.GenericResponse;
import br.com.finsavior.grpc.tables.TableDataServiceGrpc;
import br.com.finsavior.model.dto.AiAdviceDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.service.AiAdviceService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiAdviceServiceImpl implements AiAdviceService {

    private final UserRepository userRepository;
    private TableDataServiceGrpc.TableDataServiceBlockingStub tableDataServiceBlockingStub;
    private final Environment environment;

    @Autowired
    public AiAdviceServiceImpl(UserRepository userRepository, Environment environment) {
        this.environment = environment;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initialize() {
        String tableServiceHost = environment.getProperty("finsavior.table.service.host");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(tableServiceHost, 6565)
                .usePlaintext()
                .build();

        tableDataServiceBlockingStub = TableDataServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public ResponseEntity<GenericResponseDTO> generateAiAdviceAndInsights(AiAdviceDTO aiAdvice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        AiAdviceRequest aiAdviceRequest = AiAdviceRequest.newBuilder()
                .setUserId(user.getId())
                .setPrompt(aiAdvice.getPrompt())
                .setDate(aiAdvice.getDate())
                .setAccountTypeId(user.getAccountType().getId().intValue())
                .build();

        try {
            GenericResponse genericResponse = tableDataServiceBlockingStub.generateAiAdviceAndInsights(aiAdviceRequest);
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.OK.toString(), genericResponse.getMessage());
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            log.error(e.getStatus().getDescription());
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getStatus().getDescription());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<GenericResponseDTO> getAiAdviceAndInsights() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        AiAdviceRequest aiAdviceRequest = AiAdviceRequest.newBuilder()
                .setUserId(user.getId())
                .build();

        try {
            GenericResponse genericResponse = tableDataServiceBlockingStub.getAiAdviceAndInsights(aiAdviceRequest);
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.OK.toString(), genericResponse.getMessage());
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            log.error(e.getStatus().getDescription());
            GenericResponseDTO response = new GenericResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getStatus().getDescription());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
