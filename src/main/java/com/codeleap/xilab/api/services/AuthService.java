package com.codeleap.xilab.api.services;

import com.codeleap.xilab.api.exceptions.InvalidResetTokenException;
import com.codeleap.xilab.api.exceptions.TokenRefreshException;
import com.codeleap.xilab.api.models.entities.auth.AuthInfo;
import com.codeleap.xilab.api.models.entities.auth.RefreshToken;
import com.codeleap.xilab.api.models.entities.auth.User;
import com.codeleap.xilab.api.models.entities.auth.UserDetailsImpl;
import com.codeleap.xilab.api.payload.request.auth.CheckResetTokenRequest;
import com.codeleap.xilab.api.payload.request.auth.ForgetPasswordRequest;
import com.codeleap.xilab.api.payload.request.auth.LoginRequest;
import com.codeleap.xilab.api.payload.request.auth.ResetPasswordRequest;
import com.codeleap.xilab.api.payload.request.auth.TokenRefreshRequest;
import com.codeleap.xilab.api.payload.response.auth.JwtResponse;
import com.codeleap.xilab.api.payload.response.auth.MessageResponse;
import com.codeleap.xilab.api.repository.AuthRepository;
import com.codeleap.xilab.api.security.jwt.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder encoder;

	@Autowired
    JwtUtils jwtUtils;

	@Autowired
	RefreshTokenService refreshTokenService;

    @Autowired
    AuthRepository authRepository;

    @Value("${xilab.app.forgotPasswordTokenExpirationHours}")
    private Long forgotPasswordTokenExpirationHours;

    public ResponseEntity<?> forgetPassword(ForgetPasswordRequest request) {
        Optional<AuthInfo> authInfo = authRepository.findByUsername(request.getEmail());
        if (!authInfo.isPresent()) {
            log.error("User {} does not exist", request.getEmail());
            return ResponseEntity.ok().build();
        }

        String resetToken = UUID.randomUUID().toString();
        AuthInfo auth = authInfo.get();
        auth.setResetPasswordOn(LocalDateTime.now());
        auth.setResetPasswordToken(resetToken);
        authRepository.save(auth);
        refreshTokenService.deleteByUserIdOrUsername(null, request.getEmail());
        String resetPasswordUrl = request.getResetPasswordUrl().trim();
        if (!resetPasswordUrl.endsWith("/")) {
            resetPasswordUrl += "/";
        }
        String fullResetPasswordUrl = resetPasswordUrl + resetToken;
        try {
            emailService.sendResetPasswordEmail(auth.getUser().getFirstName(), auth.getUsername(), fullResetPasswordUrl,
                    forgotPasswordTokenExpirationHours.intValue());
        }
        catch (Exception ex) {
            throw new RuntimeException("Cannot send forgot password email to user: " + auth.getUsername());
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) throws RuntimeException {
        Optional<AuthInfo> authInfo = authRepository.findByResetPasswordToken(request.getResetToken());
        if (!authInfo.isPresent()) {
            throw new InvalidResetTokenException("The token is not existed");
        }

        AuthInfo auth = authInfo.get();
        LocalDateTime resetTime = auth.getResetPasswordOn();
        LocalDateTime expiredTime = resetTime.plusHours(forgotPasswordTokenExpirationHours);
        if (expiredTime.isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("The token is expired");
        }

        String newPassword = encoder.encode(request.getNewPassword());
        auth.setResetPasswordOn(null);
        auth.setResetPasswordToken(null);
        auth.setPassword(newPassword);
        authRepository.save(auth);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> isForgotPasswordTokenValid(CheckResetTokenRequest request) {
        Optional<AuthInfo> authInfo = authRepository.findByResetPasswordToken(request.getResetToken());
        if (!authInfo.isPresent()) {
            throw new InvalidResetTokenException("The token is not existed");
        }

        LocalDateTime resetTime = authInfo.get().getResetPasswordOn();
        LocalDateTime expiredTime = resetTime.plusHours(forgotPasswordTokenExpirationHours);
        if (expiredTime.isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("The token is expired");
        }

        return ResponseEntity.ok().build();
    }

	public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		}
		catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username or password is not correct");
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		String jwt = jwtUtils.generateJwtToken(userDetails);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

		return ResponseEntity.ok((new JwtResponse().toBuilder()
                .token(jwt)
                .refreshToken(refreshToken.getToken())
				.id(userDetails.getId())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName()))
                .build());
	}

	public ResponseEntity<?> refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(requestRefreshToken);
        if (!refreshTokenOptional.isPresent() || refreshTokenOptional.isEmpty()) {
            throw new TokenRefreshException(request.getRefreshToken(), "Refresh token is not valid");
        }

        RefreshToken refreshToken = refreshTokenOptional.get();
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        refreshToken = refreshTokenService.createRefreshToken(refreshToken.getUser().getId());
        User user = refreshToken.getUser();
        String jwt = jwtUtils.generateTokenFromUsername(user);

        return ResponseEntity.ok((new JwtResponse().toBuilder().token(jwt).refreshToken(refreshToken.getToken())
                .id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName())
        )
                .build());
    }

	public ResponseEntity<?> logoutUser(String username) {
		refreshTokenService.deleteByUserIdOrUsername(null, username);
		return ResponseEntity.ok(new MessageResponse("Log out successful!"));
	}

}
