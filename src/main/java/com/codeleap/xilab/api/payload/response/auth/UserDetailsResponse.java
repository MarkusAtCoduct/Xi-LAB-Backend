package com.codeleap.xilab.api.payload.response.auth;

import com.codeleap.xilab.api.models.entities.auth.User;
import com.codeleap.xilab.api.payload.response.data.BadgeItem;
import com.codeleap.xilab.api.utils.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserDetailsResponse {

	private Long userId;

	private String username;

	private String lastName;

	private String firstName;

	private List<BadgeItem> badges;

	private String email;

	private String industry;

	private String currentJob;

	private Short yearsOfExperience;

	private String aboutMe;

    private String linkedinLink;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mainAvatarUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String thumbnailAvatarUrl;

    public UserDetailsResponse(User user){
        this.userId = user.getId();
        this.username = user.getAuthInfo().getUsername();
        this.lastName = user.getLastName();
        this.firstName = user.getFirstName();
        this.email = user.getEmail();
        this.currentJob = user.getCurrentJob();
        this.industry = user.getIndustry();
        this.yearsOfExperience = user.getYearsOfExperience();
        this.aboutMe = user.getAboutMe();
        this.linkedinLink = user.getLinkedinLink();
        this.badges = getBadgeItems(user);
    }

    private List<BadgeItem> getBadgeItems(User user){
        if(CollectionUtils.isNullOrNoItem(user.getMyBadges()))
            return null;

        return user.getMyBadges().stream()
                .map(x -> new BadgeItem(x))
                .collect(Collectors.toList());
    }
}
