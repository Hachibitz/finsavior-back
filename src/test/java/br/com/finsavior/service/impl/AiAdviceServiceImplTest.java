package br.com.finsavior.service.impl;

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
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import org.grpcmock.GrpcMock;
import org.grpcmock.junit5.InProcessGrpcMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@ExtendWith({InProcessGrpcMockExtension.class, MockitoExtension.class})
class AiAdviceServiceImplTest {

    @InjectMocks
    private AiAdviceServiceImpl aiAdviceServiceImpl;

    @Mock
    private UserRepository userRepository;

    private ManagedChannel channel;

    @BeforeEach
    public void before() {
        Authentication authentication = mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("finsaviorapp");
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        channel = InProcessChannelBuilder.forName(GrpcMock.getGlobalInProcessName())
                .usePlaintext()
                .build();
        TableDataServiceGrpc.TableDataServiceBlockingStub tableDataServiceBlockingStub = TableDataServiceGrpc.newBlockingStub(channel);
        aiAdviceServiceImpl.setTableDataServiceBlockingStub(tableDataServiceBlockingStub);
    }

    @AfterEach
    public void shutdownChannel() {
        Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdownNow);
    }

    @Test
    void generateAiAdviceAndInsightsSuccess() {
        AiAdviceDTO aiAdviceDTO = AiAdviceDTO.builder()
                .analysisTypeId(1)
                .cardTable("cardTable")
                .mainAndIncomeTable("mainAndIncomeTable")
                .date("May 2024")
                .finishDate(LocalDateTime.now())
                .startDate(LocalDateTime.now())
                .temperature(0.0)
                .build();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AiAdviceResponse aiAdviceResponse = buildAiAdviceResponse();
        User user = buildUser();

        Mockito.when(userRepository.findByUsername(authentication.getName())).thenReturn(user);
        GrpcMock.stubFor(
                GrpcMock.unaryMethod(TableDataServiceGrpc.getGenerateAiAdviceAndInsightsMethod())
                        .willReturn(GrpcMock.response(aiAdviceResponse))
        );

        ResponseEntity<AiAdviceResponseDTO> response = aiAdviceServiceImpl.generateAiAdviceAndInsights(aiAdviceDTO);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.nonNull(response.getBody()));
        Assertions.assertEquals((long) response.getBody().getId(), aiAdviceResponse.getAnalysisId());
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

    private AiAdviceRequest buildAiAdviceRequest(User user) {
        return AiAdviceRequest.newBuilder()
                .setUserId(user.getId())
                .setPrompt("prompt")
                .setDate("May 2024")
                .setPlanId(user.getUserPlan().getPlanId())
                .setAnalysisTypeId(1)
                .setTemperature(0.0)
                .setStartDate(LocalDateTime.now().toString())
                .setFinishDate(LocalDateTime.now().toString())
                .build();
    }
}
