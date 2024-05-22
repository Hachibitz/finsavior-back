package br.com.finsavior.controller;

import br.com.finsavior.grpc.tables.GenericResponse;
import br.com.finsavior.model.dto.ChangePasswordRequestDTO;
import br.com.finsavior.model.dto.DeleteAccountRequestDTO;
import br.com.finsavior.model.dto.GenericResponseDTO;
import br.com.finsavior.model.dto.ProfileDataDTO;
import br.com.finsavior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> deleteAccountAndData(@RequestBody DeleteAccountRequestDTO deleteAccountRequest) {
        return userService.deleteAccount(deleteAccountRequest);
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
}
