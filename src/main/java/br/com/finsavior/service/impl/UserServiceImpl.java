package br.com.finsavior.service.impl;

import br.com.finsavior.exception.DeleteUserException;
import br.com.finsavior.exception.BusinessException;
import br.com.finsavior.exception.UpdateProfileException;
import br.com.finsavior.grpc.user.UserServiceGrpc;
import br.com.finsavior.grpc.user.UserServiceGrpc.UserServiceBlockingStub;
import br.com.finsavior.grpc.user.ChangePasswordRequest;
import br.com.finsavior.grpc.tables.GenericResponse;
import br.com.finsavior.model.dto.*;
import br.com.finsavior.model.entities.*;
import br.com.finsavior.model.enums.Flag;
import br.com.finsavior.model.enums.PlanType;
import br.com.finsavior.model.enums.UserAccountDeleteStatus;
import br.com.finsavior.producer.DeleteAccountProducer;
import br.com.finsavior.repository.*;
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
import java.util.Optional;

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
    private final UserDeleteRepository userDeleteRepository;

    private UserServiceBlockingStub userServiceBlockingStub;
    private final static String APP_ID = "finsavior-app";

    @Autowired
    public UserServiceImpl(UserRepository userRepository, DeleteAccountProducer deleteAccountProducer,
                           Environment environment, UserProfileRepository userProfileRepository,
                           PlanRepository planRepository, PlanHistoryRepository planHistoryRepository,
                           UserDeleteRepository userDeleteRepository) {
        this.userRepository = userRepository;
        this.deleteAccountProducer = deleteAccountProducer;
        this.environment = environment;
        this.userProfileRepository = userProfileRepository;
        this.planRepository = planRepository;
        this.planHistoryRepository = planHistoryRepository;
        this.userDeleteRepository = userDeleteRepository;
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
    public void deleteAccount(DeleteAccountRequestDTO deleteAccountRequestDTO) {
        User user = validateDeleteAccountRequest(deleteAccountRequestDTO);
        UserDelete userDelete = userDeleteRepository.findByUserId(user.getId());

        if(userDelete != null) {
            userDelete.setUserUpdDtm(LocalDateTime.now());
            userDelete.setUserUpdId(APP_ID);
            userDelete.setStatusId(UserAccountDeleteStatus.IN_PROCESS.getId());
        } else {
            userDelete = UserDelete.builder()
                    .userId(user.getId())
                    .statusId(UserAccountDeleteStatus.IN_PROCESS.getId())
                    .userInsId(APP_ID)
                    .userInsDtm(LocalDateTime.now())
                    .userUpdId(APP_ID)
                    .userUpdDtm(LocalDateTime.now())
                    .delFg(Flag.N)
                    .build();
        }

        userDeleteRepository.save(userDelete);

        try {
            deleteAccountProducer.sendMessage(deleteAccountRequestDTO);
            log.info("Exclusão do usuário {} {}", deleteAccountRequestDTO.getUsername(), " enviada para a fila com sucesso.");
        } catch (Exception e) {
            log.error("method: {}, message: {},error: {}", "deleteAccount", "falha no envio para a fila", e.getMessage());
            throw new DeleteUserException("Erro na exclusão, tente novamente em alguns minutos.");
        }
    }

    private User validateDeleteAccountRequest(DeleteAccountRequestDTO request) {
        if(!request.isConfirmation()) throw new DeleteUserException("Erro na exclusão: confirmação necessária.");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        if(!(user.getUsername().equals(request.getUsername())) ||
                !(user.getPassword().equals(request.getPassword()))) throw new DeleteUserException("Usuário ou senha incorretos");


        Optional<UserDelete> userDelete = Optional.ofNullable(userDeleteRepository.findByUserId(user.getId()));
        UserAccountDeleteStatus accountDeleteStatus = null;

        if(userDelete.isPresent()) {
            accountDeleteStatus = UserAccountDeleteStatus.fromId(userDelete.get().getStatusId());
        }

        if(accountDeleteStatus != null && accountDeleteStatus.equals(UserAccountDeleteStatus.IN_PROCESS))
            throw new DeleteUserException("Erro na exclusão: já existe uma solicitação em andamento.");

        return user;
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
            PlanChangeHistory planChangeHistory = getPlanchangeHistory(externalUserdto, planId);
            setProfileAndPlan(user, planId);

            userRepository.save(user);

            planHistoryRepository.save(planChangeHistory);

            log.info("method = updateUserPlan, message = Plano do user: {}, atualizado com sucesso!", externalUserdto.getUserId());
        }catch (Exception e){
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void updateProfile(MultipartFile profilePicture, UpdateProfileRequestDTO updateProfileRequest) {
        try {
            if(profilePicture != null) {
                uploadProfilePicture(profilePicture);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByUsername(authentication.getName());

            if(updateProfileRequest.getFirstName() != null) {
                user.setFirstName(updateProfileRequest.getFirstName());
            }

            if(updateProfileRequest.getLastName() != null) {
                user.setLastName(updateProfileRequest.getLastName());
            }

            user.getUserProfile().setName(user.getFirstName()+user.getLastName());

            userRepository.save(user);
        } catch (UpdateProfileException e) {
            log.error("c={}, m={}, msg={}", this.getClass().getSimpleName(), "updateProfile", e.getMessage());
            throw(e);
        }
    }

    private PlanChangeHistory getPlanchangeHistory(ExternalUserDTO externalUserdto, String planId) {
        return PlanChangeHistory.builder()
                .planId(planId)
                .userId(externalUserdto.getUserId())
                .externalUserId(externalUserdto.getExternalUserId())
                .planType(PlanType.fromValue(externalUserdto.getPlanId()).getPlanTypeId())
                .updateTime(LocalDateTime.now())
                .delFg(Flag.N)
                .userInsDtm(LocalDateTime.now())
                .userInsId(APP_ID)
                .userUpdDtm(LocalDateTime.now())
                .userUpdId(APP_ID)
                .build();
    }

    private void setProfileAndPlan(User user, String planId) {
        user.getUserPlan().setPlanId(planId);
        user.getUserPlan().setUserUpdDtm(LocalDateTime.now());
        user.getUserPlan().setUserUpdId(APP_ID);

        user.getUserProfile().setPlanId(planId);
        user.getUserProfile().setUserUpdDtm(LocalDateTime.now());
        user.getUserProfile().setUserUpdId(APP_ID);
    }
}
