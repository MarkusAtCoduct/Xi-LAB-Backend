package com.codeleap.xilab.api.payload.response;

import com.codeleap.xilab.api.payload.response.auth.JwtResponse;
import com.codeleap.xilab.api.payload.response.auth.UserDetailsResponse;
import com.codeleap.xilab.api.payload.response.method.CommentItemResponse;
import com.codeleap.xilab.api.payload.response.method.MethodResponse;
import com.codeleap.xilab.api.payload.response.method.ProcessResponse;
import com.codeleap.xilab.api.payload.response.method.RatingItemResponse;

import java.util.List;

import lombok.Data;

@Data
public class ResponseDoc {

    @Data
    public class DataObject<T> {

        private T data;

    }

    @Data
    public class PagingDataObject<T> {

        private T data;

        private Pagination pagination;
    }

    @Data
    public class MessageOnly {

        private String message;

    }

    @Data
    public class KeyValue {

        private Object keyName;

    }

    public class UserDetailsResponseDoc extends DataObject<UserDetailsResponse> {

    }

    public class JwtResponseDoc extends DataObject<JwtResponse> {

    }

    public class PagingMethodList extends PagingDataObject<List<MethodResponse>> {

    }

    public class MethodDoc extends DataObject<MethodResponse> {

    }

    public class CommentDoc extends DataObject<CommentItemResponse> {

    }

    public class RatingDoc extends DataObject<RatingItemResponse> {

    }

    public class RatingList extends DataObject<List<RatingItemResponse>> {

    }

    public class CommentList extends DataObject<List<CommentItemResponse>> {

    }

    public class ProcessDoc extends DataObject<ProcessResponse> {

    }

}