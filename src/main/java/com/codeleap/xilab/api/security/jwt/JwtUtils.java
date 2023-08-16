package com.codeleap.xilab.api.security.jwt;

import com.codeleap.xilab.api.models.entities.auth.User;
import com.codeleap.xilab.api.models.entities.auth.UserDetailsImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${xilab.app.jwtSecret}")
	private String jwtSecret;

	@Value("${xilab.app.jwtExpirationSeconds}")
	private int jwtExpirationSeconds;

	@Value("${xilab.app.jwtEnv}")
	private String jwtEnv;

	private static final String JWT_ENV_KEY = "env";

    public String generateJwtToken(UserDetailsImpl userPrincipal) {
        List<String> roles = userPrincipal.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());
        Map<String, Object> claims = getClaims(roles, userPrincipal.getFirstName(), userPrincipal.getLastName(),
                userPrincipal.getUsername(), userPrincipal.getId(),
                userPrincipal.getEmail(), jwtEnv);

        return getJwtString(userPrincipal.getUsername(), claims, (jwtExpirationSeconds * 1000));
    }

    public String generateTokenFromUsername(User user) {
        List<String> roles = user.getRoles().stream().map(role -> role.getName().toString())
                .collect(Collectors.toList());
        Map<String, Object> claims = getClaims(roles, user.getFirstName(), user.getLastName(), user.getAuthInfo().getUsername(),
                user.getId(), user.getEmail(), jwtEnv);
        return getJwtString(user.getAuthInfo().getUsername(), claims, jwtExpirationSeconds * 1000);
    }

    private HashMap<String, Object> getClaims(List<String> roles, String userPrincipal, String userPrincipal1,
                                              String userPrincipal2, Long userPrincipal3, String userPrincipal5, String jwtEnv) {
        return new HashMap<String, Object>() {
            {
                put("roles", roles);
                put("firstName", userPrincipal);
                put("lastName", userPrincipal1);
                put("username", userPrincipal2);
                put("userId", userPrincipal3);
                put("email", userPrincipal5);
                put(JWT_ENV_KEY, jwtEnv);
            }
        };
    }

    private String getJwtString(String user, Map<String, Object> claims, int jwtExpirationSeconds) {
        return Jwts.builder().setSubject(user).setIssuedAt(new Date()).addClaims(claims)
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationSeconds))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

	public UserDetailsImpl getUserDetailsFromJwtToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
		List<GrantedAuthority> authorities = (ArrayList) claims.get("roles", ArrayList.class).stream()
				.map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList());

		return UserDetailsImpl.build(claims.getSubject(), Long.valueOf(claims.get("userId", Integer.class)),
				claims.get("email", String.class), claims.get("firstName", String.class),
				claims.get("lastName", String.class), authorities);
	}

	public JwtValidationResult validateJwtToken(String authToken) {
		try {
			var claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			if (claims.getBody().containsKey(JWT_ENV_KEY)) {
				var env = claims.getBody().get(JWT_ENV_KEY, String.class);
				if (!jwtEnv.equals(env)) {
					logger.error("Invalid JWT Environment");
					return JwtValidationResult.InvalidEnvironment;
				}
			}
			return JwtValidationResult.Ok;
		}
		catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
			return JwtValidationResult.InvalidSignature;
		}
		catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
			return JwtValidationResult.InvalidToken;
		}
		catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
			return JwtValidationResult.TokenIsExpired;
		}
		catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
			return JwtValidationResult.TokenIsUnsupported;
		}
		catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
			return JwtValidationResult.ClaimsStringEmpty;
		}
		catch (Exception e) {
			logger.error("Unknown Issue: {}", e.getMessage());
			return JwtValidationResult.UnknownIssue;
		}
	}

}
