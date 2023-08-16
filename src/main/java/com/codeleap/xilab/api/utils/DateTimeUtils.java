package com.codeleap.xilab.api.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class DateTimeUtils {

	private DateTimeUtils() {
	}

	public static Long localDateTimeToUtcLong(LocalDateTime input) {
		if (input == null)
			return null;

		return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static LocalDateTime getDateInPast(Long days) {
		var now = LocalDateTime.now();
		return LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0).plusDays(-1 * days);
	}

}
