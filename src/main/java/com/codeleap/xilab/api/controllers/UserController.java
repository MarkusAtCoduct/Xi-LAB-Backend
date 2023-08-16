package com.codeleap.xilab.api.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;

import com.codeleap.xilab.api.models.entities.auth.UserDetailsImpl;
import com.codeleap.xilab.api.payload.request.auth.CheckResetTokenRequest;
import com.codeleap.xilab.api.payload.request.auth.ForgetPasswordRequest;
import com.codeleap.xilab.api.payload.request.auth.LoginRequest;
import com.codeleap.xilab.api.payload.request.auth.ResetPasswordRequest;
import com.codeleap.xilab.api.payload.request.auth.SignupRequest;
import com.codeleap.xilab.api.payload.request.auth.TokenRefreshRequest;
import com.codeleap.xilab.api.payload.request.auth.UpdateProfileRequest;
import com.codeleap.xilab.api.payload.response.BaseResponse;
import com.codeleap.xilab.api.payload.response.ResponseDoc;
import com.codeleap.xilab.api.payload.response.auth.UserDetailsResponse;
import com.codeleap.xilab.api.security.jwt.JwtUtils;
import com.codeleap.xilab.api.services.AuthService;
import com.codeleap.xilab.api.services.RefreshTokenService;
import com.codeleap.xilab.api.services.UserService;
import com.codeleap.xilab.api.utils.StringUtils;
import com.codeleap.xilab.api.utils.SysUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestControllerAdvice
@RequestMapping(value = "/api/user")
public class UserController extends BaseController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
    JwtUtils jwtUtils;

	@Autowired
    RefreshTokenService refreshTokenService;

	@Autowired
    UserService userService;

	@Autowired
    AuthService authService;

	// Get user Details By username
    @Operation(summary = "Get user details by username")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return user details data",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.UserDetailsResponseDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@GetMapping("/details")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> getUserDetails(HttpServletRequest request,
                                            @Parameter(description = "If userName is null or empty, will return details info of Login User")
                                            @RequestParam(required = false) String username) {
        UserDetailsResponse userDetails;
        if (StringUtils.isNullOrWhiteSpace(username)) {
            UserDetailsImpl tokenUser = SysUtils.getLoginUser(request);
            userDetails = userService.getUserDetailsFull(tokenUser.getUsername());
        } else {
            userDetails = userService.getUserDetailsFull(username);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse(userDetails));
    }

    // Get user Details By UserID
    @Operation(summary = "Get user details by user ID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return user details data",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.UserDetailsResponseDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@GetMapping("/{userId}/details")
	public ResponseEntity<?> getUserDetails(HttpServletRequest request, @PathVariable Long userId) {
        /*UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }*/
        var userInfo = userService.getUserDetailsFull(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse(userInfo));
    }

	// Update user avatar
    @Operation(summary = "Add or update user Avatar with Binary file")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return message",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
    @PostMapping(value = "/avatar-update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUserAvatarByStream(HttpServletRequest request,
                                                      @RequestParam("image") MultipartFile image) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }

        return userService.updateUserAvatar(userDetails.getId(), image);
    }

    // Delete user avatar
    @Operation(summary = "Delete user Avatar")
    @PostMapping(value = "/avatar-delete")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserAvatar(HttpServletRequest request) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }

        return userService.deleteUserAvatar(userDetails.getId());
    }

    // Login
	@Operation(summary = "Login")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return JWT data",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.JwtResponseDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PostMapping("/log-in")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		return authService.authenticateUser(loginRequest);
	}

	// Sign-up
    @Operation(summary = "Register new account")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Successful",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PostMapping("/sign-up")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		return userService.createUser(signUpRequest);
	}

	// Update user profile
    @Operation(summary = "Update profile info")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return user details object",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.UserDetailsResponseDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PutMapping("/update")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> updateUserProfile(HttpServletRequest request, @Valid @RequestBody UpdateProfileRequest requestData) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }
		return userService.updateUserProfile(userDetails.getId(), requestData);
	}

	// Refresh token
    @Operation(summary = "Refresh token")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful and return JWT data",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.JwtResponseDoc.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
		return authService.refreshToken(request);
	}

	// Logout
    @Operation(summary = "Logout")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successful",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))),
                    @ApiResponse(responseCode = "500", description = "Unknown error",
                            content = @Content(schema = @Schema(implementation = ResponseDoc.MessageOnly.class))) })
	@PostMapping("/log-out")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        UserDetailsImpl userDetails = SysUtils.getLoginUser(request);
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Invalid request"));
        }
		return authService.logoutUser(userDetails.getUsername());
	}


    @Operation(summary = "Send reset password email to user")
    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        return authService.forgetPassword(request);
    }

    @Operation(summary = "Do reset the user password with a new one")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }

    @Operation(summary = "Do change the password of current login user with a new one")
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }

    @Operation(summary = "Check if the reset password token is valid or not")
    @PostMapping("/check-reset-password-token")
    public ResponseEntity<?> checkResetPasswordToken(@Valid @RequestBody CheckResetTokenRequest request) {
        return authService.isForgotPasswordTokenValid(request);
    }
}
