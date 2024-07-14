package br.com.finsavior.service.impl;

import br.com.finsavior.exception.BusinessException;
import br.com.finsavior.grpc.tables.AiAdviceRequest;
import br.com.finsavior.grpc.tables.AiAdviceResponse;
import br.com.finsavior.grpc.tables.TableDataServiceGrpc;
import br.com.finsavior.model.dto.AiAdviceDTO;
import br.com.finsavior.model.dto.AiAdviceResponseDTO;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.model.entities.UserPlan;
import br.com.finsavior.model.entities.UserProfile;
import br.com.finsavior.model.enums.Flag;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.test.util.GrpcServicesUtil;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class AiAdviceServiceImplTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @InjectMocks
    private AiAdviceServiceImpl aiAdviceServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TableDataServiceGrpc.TableDataServiceBlockingStub blockingStub;

    private String serverName;

    private final TableDataServiceGrpc.TableDataServiceImplBase generateAiAdviceAndInsightsServiceImplSuccess =
            GrpcServicesUtil.getGenerateAiAdviceAndInsightsServiceImplSuccessCall();

    private final TableDataServiceGrpc.TableDataServiceImplBase generateAiAdviceAndInsightsServiceImplFailure =
            GrpcServicesUtil.getGenerateAiAdviceAndInsightsServiceImplFailureCall();

    @BeforeEach
    public void before() {
        Authentication authentication = mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("finsaviorapp");
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        serverName = InProcessServerBuilder.generateName();

        ManagedChannel channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
        blockingStub = TableDataServiceGrpc.newBlockingStub(channel);
        aiAdviceServiceImpl.setTableDataServiceBlockingStub(blockingStub);
    }

    @Test
    void generateAiAdviceAndInsightsSuccess() throws IOException {
        AiAdviceDTO aiAdviceDTO = buildAiAdviceDTO();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AiAdviceResponse aiAdviceResponse = buildAiAdviceResponse();
        User user = buildUser();

        Mockito.when(userRepository.findByUsername(authentication.getName())).thenReturn(user);
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(generateAiAdviceAndInsightsServiceImplSuccess).build().start());

        ResponseEntity<AiAdviceResponseDTO> response = aiAdviceServiceImpl.generateAiAdviceAndInsights(aiAdviceDTO);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.nonNull(response.getBody()));
        Assertions.assertEquals((long) response.getBody().getId(), aiAdviceResponse.getAnalysisId());
    }

    @Test
    void generateAiAdviceAndInsightsFailure() throws IOException {
        AiAdviceDTO aiAdviceDTO = buildAiAdviceDTO();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = buildUser();

        Mockito.when(userRepository.findByUsername(authentication.getName())).thenReturn(user);
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(generateAiAdviceAndInsightsServiceImplFailure).build().start());

        Assertions.assertThrows(BusinessException.class, () -> {
            aiAdviceServiceImpl.generateAiAdviceAndInsights(aiAdviceDTO);
        });
    }

    private User buildUser() {
        UserProfile userProfile = UserProfile.builder()
                .name("testildo")
                .delFg(Flag.N)
                .profilePicture(null)
                .planId("3L")
                .id(1L)
                .userInsDtm(LocalDateTime.now())
                .userInsId("APP_ID")
                .userUpdDtm(LocalDateTime.now())
                .userUpdId("APP_ID")
                .build();

        UserPlan userPlan = UserPlan.builder()
                .userId(1L)
                .planId("3L")
                .delFg(Flag.N)
                .userInsDtm(LocalDateTime.now())
                .userInsId("APP_ID")
                .userUpdDtm(LocalDateTime.now())
                .userUpdId("APP_ID")
                .build();

        return User.builder()
                .delFg(false)
                .email("test@email.com")
                .enabled(true)
                .firstName("testildo")
                .lastName("teste")
                .userProfile(userProfile)
                .userPlan(userPlan)
                .password("password")
                .id(1L)
                .build();
    }

    private AiAdviceResponse buildAiAdviceResponse() {
        return AiAdviceResponse.newBuilder()
                .setAnalysisId(1L)
                .build();
    }

    private AiAdviceDTO buildAiAdviceDTO() {
        return AiAdviceDTO.builder()
                .analysisTypeId(1)
                .cardTable("cardTable")
                .mainAndIncomeTable("mainAndIncomeTable")
                .date("May 2024")
                .finishDate(LocalDateTime.now())
                .startDate(LocalDateTime.now())
                .temperature(0.0)
                .build();
    }
}
