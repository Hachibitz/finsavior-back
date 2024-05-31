package br.com.finsavior.service.impl;

import br.com.finsavior.exception.DeleteUserException;
import br.com.finsavior.exception.BusinessException;
import br.com.finsavior.grpc.user.UserServiceGrpc;
import br.com.finsavior.grpc.user.UserServiceGrpc.UserServiceBlockingStub;
import br.com.finsavior.grpc.user.DeleteAccountRequest;
import br.com.finsavior.grpc.user.ChangePasswordRequest;
import br.com.finsavior.grpc.tables.GenericResponse;
import br.com.finsavior.model.dto.ChangePasswordRequestDTO;
import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import br.com.finsavior.model.dto.ExternalUserDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.model.dto.ProfileDataDTO;
import br.com.finsavior.model.entities.Plan;
import br.com.finsavior.model.entities.PlanChangeHistory;
import br.com.finsavior.model.entities.User;
import br.com.finsavior.model.entities.UserProfile;
import br.com.finsavior.model.enums.PlanType;
import br.com.finsavior.producer.DeleteAccountProducer;
import br.com.finsavior.repository.PlanHistoryRepository;
import br.com.finsavior.repository.PlanRepository;
import br.com.finsavior.repository.UserProfileRepository;
import br.com.finsavior.repository.UserRepository;
import br.com.finsavior.service.UserService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final static Long MAX_PROFILE_IMAGE_SIZE_KB = 5120L;

    private final UserRepository userRepository;
    private final DeleteAccountProducer deleteAccountProducer;
    private final Environment environment;
    private final UserProfileRepository userProfileRepository;
    private final PlanRepository planRepository;
    private final PlanHistoryRepository planHistoryRepository;

    private UserServiceBlockingStub userServiceBlockingStub;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, DeleteAccountProducer deleteAccountProducer, Environment environment, UserProfileRepository userProfileRepository, PlanRepository planRepository, PlanHistoryRepository planHistoryRepository) {
        this.userRepository = userRepository;
        this.deleteAccountProducer = deleteAccountProducer;
        this.environment = environment;
        this.userProfileRepository = userProfileRepository;
        this.planRepository = planRepository;
        this.planHistoryRepository = planHistoryRepository;
    }

    @PostConstruct
    public void initialize() {
        String userServiceHost = environment.getProperty("finsavior.user.service.host");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(userServiceHost, 6566)
                .usePlaintext()
                .build();

        userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public ResponseEntity<?> deleteAccount(DeleteAccountRequestDTO deleteAccountRequestDTO) {
        DeleteAccountRequest message = DeleteAccountRequest.newBuilder()
                .setUsername(deleteAccountRequestDTO.getUsername())
                .setPassword(deleteAccountRequestDTO.getPassword())
                .setConfirmation(deleteAccountRequestDTO.isConfirmation())
                .build();

        try {
            GenericResponseDTO genericResponseDTO = new GenericResponseDTO(HttpStatus.OK.name(), "Conta adicionada na fila de exclusão com sucesso. Exclusão será processada nas próximas horas junto com todos os dados da conta.");
            deleteAccountProducer.sendMessage(message);
            log.info("Exclusão do usuário "+deleteAccountRequestDTO.getUsername()+" enviada para a fila com sucesso.");
            return ResponseEntity.ok(genericResponseDTO);
        } catch (StatusRuntimeException e) {
            log.error("Erro na exclusão, tente novamente em alguns minutos."+e.getStatus().getDescription());
            throw new DeleteUserException("Erro na exclusão: " + e.getStatus().getDescription());
        } catch (Exception e) {
            log.error("Erro na exclusão, tente novamente em alguns minutos."+e.getMessage());
            throw new DeleteUserException("Erro na exclusão, tente novamente em alguns minutos.");
        }
    }

    @Override
    public ResponseEntity<?> changeAccountPassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .setUsername(user.getUsername())
                .setCurrentPassword(changePasswordRequestDTO.getCurrentPassword())
                .setNewPassword(changePasswordRequestDTO.getNewPassword())
                .build();

        try {
            GenericResponse response = userServiceBlockingStub.changeAccountPassword(changePasswordRequest);
            return ResponseEntity.ok(response.getMessage());
        } catch (StatusRuntimeException e) {
            log.error("Erro ao realizar alteração de senha: {}", e.getStatus().getDescription());
            throw new BusinessException("Erro ao realizar alteração de senha: " + e.getStatus().getDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<GenericResponseDTO> uploadProfilePicture(MultipartFile profileData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        UserProfile userProfile = userProfileRepository.getByUserId(user.getId());
        GenericResponseDTO response = new GenericResponseDTO();

        if((profileData.getSize()/1024) > MAX_PROFILE_IMAGE_SIZE_KB) {
            response.setMessage("Tamanho máximo do arquivo excedido: 5MB");
            response.setStatus(HttpStatus.BAD_REQUEST.name());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            userProfile.setProfilePicture(profileData.getBytes());
            userProfileRepository.save(userProfile);

            response.setMessage("File saved succesfully");
            response.setStatus(HttpStatus.CREATED.name());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Failed to save file: {}", e.getMessage());
            response.setMessage("Failed to save the file: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<ProfileDataDTO> getProfileData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        byte[] profilePictureBytes = user.getUserProfile().getProfilePicture();
        String profilePictureBase64 = null;

        if(profilePictureBytes != null) {
            profilePictureBase64 = Base64.getEncoder().encodeToString(profilePictureBytes);
        }

        Plan plan = planRepository.getById(user.getUserProfile().getPlanId());

        ProfileDataDTO responseBody = ProfileDataDTO.builder()
                .username(user.getFirstAndLastName())
                .profilePicture(profilePictureBase64)
                .plan(plan)
                .email(user.getEmail())
                .build();

        return ResponseEntity.ok(responseBody);
    }

    @Override
    public void updateUserPlan(ExternalUserDTO externalUserdto){
        User user = userRepository.getById(externalUserdto.getUserId());
        String planId = externalUserdto.getPlanId();
        try {
            PlanChangeHistory planChangeHistory = PlanChangeHistory.builder()
                    .planId(planId)
                    .userId(externalUserdto.getUserId())
                    .externalUserId(externalUserdto.getExternalUserId())
                    .planType(PlanType.fromValue(externalUserdto.getPlanId()).getPlanTypeId())
                    .updateTime(LocalDateTime.now())
                    .build();

            user.getUserPlan().setPlanId(planId);
            user.getUserProfile().setPlanId(planId);
            userRepository.save(user);
            planHistoryRepository.save(planChangeHistory);
            log.info("method = updateUserPlan, message = Plano do user: {}, atualizado com sucesso!", externalUserdto.getUserId());
        }catch (Exception e){
            throw new BusinessException(e.getMessage());
        }
    }
}
