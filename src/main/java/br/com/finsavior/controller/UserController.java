package br.com.finsavior.controller;

import br.com.finsavior.grpc.tables.GenericResponse;
import br.com.finsavior.model.dto.*;
import br.com.finsavior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/delete-account")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccountAndData(@RequestBody DeleteAccountRequestDTO deleteAccountRequest) {
        userService.deleteAccount(deleteAccountRequest);
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changeAccountPassword(@RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {
        return userService.changeAccountPassword(changePasswordRequestDTO);
    }
    @PostMapping("/profile-data/upload-picture")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponseDTO> uploadProfilePicture(@RequestParam MultipartFile profilePicture) {
        return userService.uploadProfilePicture(profilePicture);
    }

    @GetMapping("/get-profile-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProfileDataDTO> getProfileData() {
        return userService.getProfileData();
    }

    @PatchMapping("/profile-data/update-profile")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public void updateProfile(
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName) {

        UpdateProfileRequestDTO updateProfileRequest = new UpdateProfileRequestDTO();
        updateProfileRequest.setFirstName(firstName);
        updateProfileRequest.setLastName(lastName);

        userService.updateProfile(profilePicture, updateProfileRequest);
    }
}
