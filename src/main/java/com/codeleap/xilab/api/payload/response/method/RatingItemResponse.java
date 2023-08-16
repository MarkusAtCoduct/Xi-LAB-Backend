package com.codeleap.xilab.api.payload.response.method;

import com.codeleap.xilab.api.models.entities.Rating;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class RatingItemResponse
{
    private Long id;

    private Long methodId;

    private String ratedBy;

    private Long ratedByUserId;

    private String ratedByUserEmail;

    private List<String> raterBadges;

    private String raterAvatarUrl;

    private String message;

    private Short score;

    private String headline;

    private LocalDateTime ratedOn;

    public RatingItemResponse(Rating ratingEntity){
        this.id = ratingEntity.getId();
        this.methodId = ratingEntity.getMethodId();
        this.message = ratingEntity.getMessage();
        this.ratedBy = ratingEntity.getRatedByUser().getFullName();
        this.ratedByUserId = ratingEntity.getRatedByUser().getId();
        this.ratedByUserEmail = ratingEntity.getRatedByUser().getEmail();
        this.ratedOn = ratingEntity.getRatedOn();
        this.raterBadges = ratingEntity.getRatedByUser().getBadgeNames();
        this.headline = ratingEntity.getHeadline();
        this.score = ratingEntity.getScore();
    }
}
