package com.codeleap.xilab.api.payload.response.data;

import com.codeleap.xilab.api.models.entities.UserBadge;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeItem {

	private String badgeType;

	private LocalDateTime achieveOn;

	public BadgeItem(UserBadge userBadge){
	    if(userBadge == null)
	        return;

	    badgeType = userBadge.getBadgeType();
	    achieveOn = userBadge.getAchievedOn();
    }
}
