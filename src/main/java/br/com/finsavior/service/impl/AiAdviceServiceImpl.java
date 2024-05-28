package br.com.finsavior.service.impl;

import br.com.finsavior.exception.BusinessException;
import br.com.finsavior.grpc.tables.AiAdviceRequest;
import br.com.finsavior.grpc.tables.GenericResponse;
import br.com.finsavior.grpc.tables.TableDataServiceGrpc;
import br.com.finsavior.model.dto.AiAdviceDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.model.enums.AnalysisType;
import br.com.finsavior.model.enums.Prompt;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

        AnalysisType chosenAnalysis = getChosenAnalysis(aiAdvice);

        if(!validateAnalysisTypeAndPlan(chosenAnalysis, user)) {
            log.error("class: AiAdviceServiceImpl, method: generateAiAdviceAndInsights, message: Plano do perfil não possui essa cobertura");
            throw new BusinessException("Plano do perfil não possui essa cobertura", HttpStatus.EXPECTATION_FAILED);
        }

        String prompt = getPrompt(aiAdvice, user);

        AiAdviceRequest aiAdviceRequest = AiAdviceRequest.newBuilder()
                .setUserId(user.getId())
                .setPrompt(prompt)
                .setDate(aiAdvice.getDate())
                .setPlanId(Integer.parseInt(user.getUserPlan().getPlanId()))
                .setAnalysisTypeId(chosenAnalysis.getAnalysisTypeId())
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

    private String getPrompt(AiAdviceDTO aiAdvice, User user) {
        AnalysisType chosenAnalysis = getChosenAnalysis(aiAdvice);

        List<String> promptParts = getPromptByAnalysisType(chosenAnalysis).getPromptParts();
        return getFormattedPrompt(promptParts, aiAdvice);
    }

    private Prompt getPromptByAnalysisType(AnalysisType analysisType) {
        return Arrays.stream(Prompt.values())
                .filter(prompt -> prompt.getAnalysisType().equals(analysisType))
                .findFirst()
                .orElse(null);
    }

    private String getFormattedPrompt(List<String> promptParts, AiAdviceDTO aiAdvice) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(promptParts.get(0))
                .append(aiAdvice.getMainAndIncomeTable()).append("\n\n")
                .append(promptParts.get(1)).append("\n\n")
                .append(aiAdvice.getCardTable()).append("\n\n")
                .append(promptParts.get(2))
                .append(promptParts.get(3));

        return prompt.toString();
    }

    private boolean validateAnalysisTypeAndPlan(AnalysisType chosenAnalysis, User user) {
        return chosenAnalysis.getPlansCoverageList().stream()
                .anyMatch(planType -> planType.getPlanTypeId().equals(user.getUserProfile().getPlanId()));
    }

    private AnalysisType getChosenAnalysis(AiAdviceDTO aiAdvice) {
        AnalysisType chosenAnalysis = Arrays.stream(AnalysisType.values())
                .filter((type -> Objects.equals(type.getAnalysisTypeId(), aiAdvice.getAnalysisTypeId())))
                .findFirst()
                .orElse(null);

        assert chosenAnalysis != null;
        return chosenAnalysis;
    }
}
