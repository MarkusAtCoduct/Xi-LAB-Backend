package com.codeleap.xilab.api.utils;

public final class DataUtils {

	private DataUtils() {
	}

	public static boolean isYes(String input) {
        if (StringUtils.isNullOrWhiteSpace(input))
            return false;

        input = input.trim().toLowerCase();
        if (input.equals("yes") || input.equals("y"))
            return true;

        return false;
    }

	public static boolean isTrue(Boolean boolObj){
	    return boolObj != null && boolObj;
    }

    public static boolean isFalse(Boolean boolObj){
	    return boolObj != null && !boolObj;
    }

}
