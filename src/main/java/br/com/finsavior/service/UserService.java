package br.com.finsavior.service;

import br.com.finsavior.model.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {
    public void deleteAccount(DeleteAccountRequestDTO deleteAccountRequestDTO);
    public ResponseEntity<?> changeAccountPassword(ChangePasswordRequestDTO changePasswordRequestDTO);
    public ResponseEntity<GenericResponseDTO> uploadProfilePicture(MultipartFile profileData);
    public ResponseEntity<ProfileDataDTO> getProfileData();
    public void updateUserPlan(ExternalUserDTO externalUserdto);
    public void updateProfile(MultipartFile profilePicture, UpdateProfileRequestDTO updateProfileRequest);
}
