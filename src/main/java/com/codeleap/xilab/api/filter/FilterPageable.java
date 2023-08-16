package com.codeleap.xilab.api.filter;

import com.codeleap.xilab.api.utils.StringUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

public final class FilterPageable {
    private FilterPageable(){

    }

    private static final Map<String, String> METHOD_SORTING_FIELD_MAP = new HashMap<>(){{
       put("NAME","name");
       put("COST","cost");
       put("TIME","time");
       put("RATE","averageRating");
       put("PHASE","phase");
    }};

    public static Pageable generateMethodPageable(int pNo, int noOfItems, String sortByKey, String sortDirection) {
        if(StringUtils.isNullOrWhiteSpace(sortByKey))
            return null;

        sortByKey = sortByKey.trim().toUpperCase();
        if(METHOD_SORTING_FIELD_MAP.containsKey(sortByKey)){
            return generatePageable(pNo, noOfItems, METHOD_SORTING_FIELD_MAP.get(sortByKey), sortDirection);
        }

        return null;
    }

    public static Pageable generatePageable(int pNo, int noOfItems, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(pNo, noOfItems, sort);
    }
}
