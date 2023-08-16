package com.codeleap.xilab.api.services;

import com.codeleap.xilab.api.models.StorageImageData;
import com.codeleap.xilab.api.models.StorageImageInfo;
import com.codeleap.xilab.api.models.entities.auth.UserAvatar;
import com.codeleap.xilab.api.payload.request.auth.UpdateProfileRequest;
import com.codeleap.xilab.api.payload.response.BaseResponse;
import com.codeleap.xilab.api.repository.AuthRepository;
import com.codeleap.xilab.api.repository.RoleRepository;
import com.codeleap.xilab.api.repository.UserAvatarRepository;
import com.codeleap.xilab.api.repository.UserRepository;
import com.codeleap.xilab.api.models.entities.auth.AuthInfo;
import com.codeleap.xilab.api.models.entities.auth.ERole;
import com.codeleap.xilab.api.models.entities.auth.Role;
import com.codeleap.xilab.api.models.entities.auth.User;
import com.codeleap.xilab.api.payload.request.auth.SignupRequest;
import com.codeleap.xilab.api.payload.response.auth.MessageResponse;
import com.codeleap.xilab.api.payload.response.auth.UserDetailsResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
    UserRepository userRepository;

	@Autowired
    UserAvatarRepository userAvatarRepository;

	@Autowired
    RoleRepository roleRepository;

	@Autowired
    AuthRepository authRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
    ImageService imageService;

	private final String tempPass = "xiLAB2023";

	public User checkAndCreateUser(String email, String firstName, String lastName) {
	    var userOpt = userRepository.findByEmail(email);
		if (userOpt.isPresent()) {
			return userOpt.get();
		}

        // Create new user's account
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .roles(roles).build();

        var newUser = userRepository.save(user);

        AuthInfo authInfo = AuthInfo.builder()
                .password(encoder.encode(tempPass))
                .user(newUser)
                .username(email)
                .build();
        authRepository.save(authInfo);

		return newUser;
	}

	public ResponseEntity createUser(SignupRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
		}

        signUpRequest.setEmail(signUpRequest.getEmail().trim().toLowerCase());

		// Create new user's account
		Set<String> strRoles = null;
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		}
		else {
			strRoles.forEach(role -> {
				switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);

						break;
					case "mod":
						Role modRole = roleRepository.findByName(ERole.ROLE_LEAD)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(modRole);

						break;
					default:
						Role userRole = roleRepository.findByName(ERole.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(userRole);
				}
			});
		}

        User user = User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .gender(signUpRequest.getGender())
                .industry(signUpRequest.getIndustry())
                .currentJob(signUpRequest.getCurrentJob())
                .yearsOfExperience(signUpRequest.getYearsOfExperience())
                .aboutMe(signUpRequest.getAboutMe())
                .linkedinLink(signUpRequest.getLinkedinLink())
                .roles(roles).build();

        var newUser = userRepository.save(user);

        AuthInfo authInfo = AuthInfo.builder()
                .password(encoder.encode(signUpRequest.getPassword()))
                .user(newUser)
                .username(signUpRequest.getEmail())
                .build();
        authRepository.save(authInfo);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

    public ResponseEntity<?> updateUserProfile(Long userId, UpdateProfileRequest request) {
        try {
            var user = userRepository.findById(userId);
            if (user.isPresent()) {
                User userInfo = user.get();
                request.updateExistedEntity(userInfo);
                userInfo = userRepository.save(userInfo);
                return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse(new UserDetailsResponse(userInfo)));
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse("User " + userId + " not found "));
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Unexpected error while updating profile for User: " + userId));
        }
    }

    public UserDetailsResponse getUserDetailsFull(String username) {
        var userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty() || userOptional.get() == null) {
            return null;
        }
        var user = userOptional.get();
        UserDetailsResponse responseUser = new UserDetailsResponse(user);

        if (user.getHasAvatar() != null && user.getHasAvatar()) {
            StorageImageInfo imageInfo = imageService.getLocalAvatarImage(user.getId());
            if (imageInfo != null) {
                responseUser.setMainAvatarUrl(imageInfo.getOriginalImageUrl())
                        .setThumbnailAvatarUrl(imageInfo.getThumbnailImageUrl());
            }
        } else {
            responseUser.setMainAvatarUrl("").setThumbnailAvatarUrl("");
        }

        return responseUser;
    }

    public UserDetailsResponse getUserDetailsFull(Long userId) {
        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty() || userOptional.get() == null) {
            return null;
        }
        var user = userOptional.get();
        UserDetailsResponse responseUser = new UserDetailsResponse(user);

        if (user.getHasAvatar() != null && user.getHasAvatar()) {
            StorageImageInfo imageInfo = imageService.getLocalAvatarImage(user.getId());
            if (imageInfo != null) {
                responseUser.setMainAvatarUrl(imageInfo.getOriginalImageUrl())
                        .setThumbnailAvatarUrl(imageInfo.getThumbnailImageUrl());
            }
        } else {
            responseUser.setMainAvatarUrl("").setThumbnailAvatarUrl("");
        }

        return responseUser;
    }

    private void updateAvatars(Long userId, StorageImageData imageData){
	    var check = userAvatarRepository.findByUserId(userId);
	    if(check.isPresent()){
	        var userAvatars = check.get();
	        userAvatars.setMainAvatar(imageData.getOriginalImage());
	        userAvatars.setThumbAvatar(imageData.getThumbnailImage());
	        userAvatarRepository.save(userAvatars);
        }else{
	        var userAvatars = new UserAvatar();
	        userAvatars.setUserId(userId);
            userAvatars.setMainAvatar(imageData.getOriginalImage());
            userAvatars.setThumbAvatar(imageData.getThumbnailImage());
            userAvatarRepository.save(userAvatars);
        }
    }

    public ResponseEntity<?> updateUserAvatar(Long userId, MultipartFile imageFile) {
        try {
            var result = userRepository.findById(userId);
            if (result.isPresent()) {
                var imageProcessResult = imageService.processAvatarImage(imageFile.getBytes(), userId);
                if (imageProcessResult == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new BaseResponse("Cannot process user avatar"));
                }

                User userInfo = result.get();
                userInfo.setHasAvatar(true);
                userRepository.save(userInfo);
                updateAvatars(userId, imageProcessResult);
                return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse("Avatar updated"));
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse("User " + userId + " not found "));
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Unexpected error while updating avatar for User: " + userId));
        }
    }

    public ResponseEntity<?> deleteUserAvatar(Long userId) {
        try {
            var result = userRepository.findById(userId);
            if (result.isPresent()) {
                boolean imageDeleteResult = imageService.deleteLocalAvatarImage(userId.toString());

                if (!imageDeleteResult) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new BaseResponse("Cannot delete user avatar"));
                }

                User userInfo = result.get();
                userInfo.setHasAvatar(false);
                userRepository.save(userInfo);
                userAvatarRepository.deleteByUserId(userId);
                return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse("Avatar deleted"));
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse("User " + userId + " not found "));
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Unexpected error while deleting avatar for User: " + userId));
        }
    }
}
