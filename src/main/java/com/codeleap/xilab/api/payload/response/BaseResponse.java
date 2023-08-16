package com.codeleap.xilab.api.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {

	private Object data;

	private String message;

	private Object additionalData;

	private Pagination pagination;

	public BaseResponse(Object data) {
		this.data = data;
	}

	public BaseResponse addExtraData(String key, Object value){
	    if(additionalData == null){
	        additionalData = new HashMap<String, Object>();
            ((HashMap)additionalData).put(key, value);
        }else{
	        if(additionalData instanceof HashMap){
                ((HashMap)additionalData).put(key, value);
            }else{
	            throw new RuntimeException("BaseResponse.additionalData is using with another datatype");
            }
        }
	    return this;
    }

	public BaseResponse(String message) {
		this.message = message;
	}

	public BaseResponse(String message, Object... params) {
	    var formattedMessage = String.format(message,params);
		this.message = formattedMessage;
	}

	public BaseResponse(Object value, String key) {
		var responseMap = new HashMap<String, Object>();
		responseMap.put(key, value);
		this.data = responseMap;
	}

}