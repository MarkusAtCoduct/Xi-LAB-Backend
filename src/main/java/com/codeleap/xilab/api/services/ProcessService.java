package com.codeleap.xilab.api.services;


import com.codeleap.xilab.api.models.entities.Method;
import com.codeleap.xilab.api.payload.request.CreateMethodRequest;
import com.codeleap.xilab.api.payload.response.BaseResponse;
import com.codeleap.xilab.api.repository.MethodRepository;
import com.codeleap.xilab.api.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessService implements InitializingBean {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    UserRepository userRepository;

    JPAQueryFactory jpaQueryFactory;

    @Override
    public void afterPropertiesSet() {
        jpaQueryFactory = new JPAQueryFactory(entityManager);
    }


    public ResponseEntity<?> createOrUpdateProcess(CreateMethodRequest createMethodRequest, Long userId) {
        try {

            HttpStatus responseStatus = HttpStatus.OK;

            return ResponseEntity.status(responseStatus).body(new BaseResponse(null));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in updating process data"));
        }
    }

    public ResponseEntity<?> getProcessDetailsInfo(Long userId) {
        try {
            Method methodInfo;
            HttpStatus responseStatus = HttpStatus.OK;

            return ResponseEntity.status(responseStatus).body(new BaseResponse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse("Error in getting process data"));
        }
    }
}
