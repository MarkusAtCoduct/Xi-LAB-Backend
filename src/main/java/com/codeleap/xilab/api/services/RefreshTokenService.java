package com.codeleap.xilab.api.services;

import com.codeleap.xilab.api.exceptions.TokenRefreshException;
import com.codeleap.xilab.api.repository.RefreshTokenRepository;
import com.codeleap.xilab.api.repository.UserRepository;
import com.codeleap.xilab.api.models.entities.auth.RefreshToken;
import com.codeleap.xilab.api.models.entities.auth.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefreshTokenService {

	@Value("${xilab.app.jwtRefreshExpirationSeconds}")
	private Long refreshTokenDurationSeconds;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	@Transactional
	public RefreshToken createRefreshToken(Long userId) {
		deleteByUserIdOrUsername(userId, "");
		RefreshToken refreshToken = new RefreshToken();

		refreshToken.setUser(userRepository.findById(userId).get());
		refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationSeconds));
		refreshToken.setToken(UUID.randomUUID().toString());

		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(),
					"Refresh token was expired. Please make a new sign-in request");
		}

		return token;
	}

	@Transactional
	public boolean deleteByUserIdOrUsername(Long userId, String username) {
		Optional<User> user;
		if (userId != null && userId > 0) {
			user = userRepository.findById(userId);
		}
		else {
			user = userRepository.findByEmail(username);
		}

		if (user.isPresent()) {
			refreshTokenRepository.deleteByUser(user.get());
			log.info("User {} logged out. ", user);
			return true;
		}
		else {
			log.info("User does not exist");
			return false;
		}
	}

}
