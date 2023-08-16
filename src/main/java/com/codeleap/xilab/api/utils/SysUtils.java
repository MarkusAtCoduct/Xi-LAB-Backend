package com.codeleap.xilab.api.utils;

import com.codeleap.xilab.api.models.entities.auth.UserDetailsImpl;
import com.codeleap.xilab.api.security.jwt.AuthTokenFilter;

import javax.servlet.http.HttpServletRequest;

public final class SysUtils {

	private SysUtils() {
	}

	public static UserDetailsImpl getLoginUser(HttpServletRequest request) {
		Object loginUserObject = request.getAttribute(AuthTokenFilter.CURRENT_VALIDATED_USER_ATTR);
		if (loginUserObject != null && loginUserObject instanceof UserDetailsImpl) {
			return (UserDetailsImpl) loginUserObject;
		}
		else {
			return null;
		}
	}

}
