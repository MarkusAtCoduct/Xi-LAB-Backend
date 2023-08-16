package com.codeleap.xilab.api.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class WordingFilterUtils {

	private WordingFilterUtils() {
	}

	private static final Set<String> ORGANIZATION_WORDING_FILTER_SET = new HashSet<>(
			Arrays.asList(",", ".", "ltd", "ltd.", ".ltd", "ltd,", ",ltd", "llc", "llc.", ".llc", "llc,", ",llc", "pvt",
					"pvt.", ".pvt", "pvt,", ",pvt", "limited"));

	public static String getOrganizationNameFilteredString(String orgName) {
		if (StringUtils.isNullOrWhiteSpace(orgName))
			return "";

		orgName = orgName.trim();
		var nameParts = orgName.split(" ");
		var builder = new StringBuffer();
		for (String namePart : nameParts) {
			if (StringUtils.isNullOrWhiteSpace(namePart))
				continue;
			if (ORGANIZATION_WORDING_FILTER_SET.contains(namePart.toLowerCase()))
				continue;

			builder.append(" " + namePart);
		}

		return builder.toString().trim();
	}

}
