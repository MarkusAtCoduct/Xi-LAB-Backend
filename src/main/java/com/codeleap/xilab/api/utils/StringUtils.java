package com.codeleap.xilab.api.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isNullOrWhiteSpace(String str) {
        return str == null || str.trim().equals("");
    }

    public static String getFullName(String firstName, String lastName){
        if(isNullOrWhiteSpace(firstName)){
            if(isNullOrWhiteSpace(lastName)){
                return "";
            }else{
                return lastName.trim();
            }
        }else{
            if(isNullOrWhiteSpace(lastName)){
                return firstName.trim();
            }else{
                return String.format("%s %s", firstName.trim(), lastName.trim());
            }
        }
    }

    public static String toDBArrayFormat(List<String> input){
        if(input == null || input.size() < 1)
            return "";
        return String.join(",", input);
    }

    public static List<String> fromDBArrayFormat(String input){
        if(isNullOrWhiteSpace(input))
            return new ArrayList<>();
        var parts = input.split(",");
        return Arrays.stream(parts).collect(Collectors.toList());
    }

    /**
     * Parse JSON string to nested generic object T< V>
     * @return T
     */
    public static <T, V> T parseNestedGeneric(String jsonString, Class<T> tClass, Class<V> vClass) {
        Type type = getType(tClass, vClass);
        Gson gson = new Gson();
        return gson.fromJson(jsonString, type);
    }

    private static Type getType(Class<?> rawClass, Class<?> parameter) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { parameter };
            }

            @Override
            public Type getRawType() {
                return rawClass;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    public static <T> T fromJson(final String jsonString, final TypeReference<T> type) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            return objectMapper.readValue(jsonString, type);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String objectToJson(Object object) {
        if (object == null)
            return null;

        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static String objectToJsonWithCustomProperty(Object object) {
        try {
            if (object == null)
                return null;

            var ow = new ObjectMapper();
            return ow.writeValueAsString(object);
        }
        catch (Exception e) {
            return null;
        }
    }

}