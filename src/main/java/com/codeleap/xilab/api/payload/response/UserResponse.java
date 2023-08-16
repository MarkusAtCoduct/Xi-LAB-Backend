package com.codeleap.xilab.api.payload.response;

import com.codeleap.xilab.api.models.entities.auth.User;
import com.codeleap.xilab.api.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String userName;

    private String email;

    private String fullName;

    private String organizationId;

    private String organizationName;

    public UserResponse(User userEntity) {
        if (userEntity == null)
            return;

        id = userEntity.getId();
        userName = userEntity.getAuthInfo().getUsername();
        email = userEntity.getEmail();
        fullName = StringUtils.getFullName(userEntity.getFirstName(), userEntity.getLastName());
    }
}
