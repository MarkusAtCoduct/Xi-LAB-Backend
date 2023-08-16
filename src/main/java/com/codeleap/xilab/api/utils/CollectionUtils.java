package com.codeleap.xilab.api.utils;

import java.util.Collection;

public final class CollectionUtils {

	private CollectionUtils() {
	}

	public static boolean isNullOrNoItem(Collection collection) {
		return (collection == null || collection.isEmpty());
	}

	public static boolean hasItems(Collection collection) {
		return (collection != null && collection.size() > 0);
	}

}
