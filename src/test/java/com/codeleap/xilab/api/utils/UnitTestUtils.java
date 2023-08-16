package com.codeleap.xilab.api.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class UnitTestUtils {

	public static String parseObjectToJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	public static String parseListToRequestParamValue(List<String> listData) {
		if (listData == null || listData.size() < 1)
			return "";

		return String.join(",", listData);
	}

	public static String parseListToRequestBodyValue(List<String> listData) {
		if (listData == null || listData.size() < 1)
			return "";

		JSONArray list = new JSONArray();
		for (int i = 0; i < listData.size(); i++) {
			list.put(listData.get(i));
		}
		return list.toString();
	}

	public static String parseListToRequestBodyValue(String name, List<String> listData) {
		if (listData == null || listData.size() < 1)
			return "";

		JSONObject jsonObject = new JSONObject();
		JSONArray list = new JSONArray();
		for (int i = 0; i < listData.size(); i++) {
			list.put(listData.get(i));
		}

		try {
			jsonObject.put(name, list);
			return jsonObject.toString();
		}
		catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

}
