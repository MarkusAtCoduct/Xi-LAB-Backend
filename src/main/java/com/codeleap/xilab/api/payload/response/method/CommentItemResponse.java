package com.codeleap.xilab.api.payload.response.method;

import com.codeleap.xilab.api.models.entities.Comment;
import com.codeleap.xilab.api.utils.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentItemResponse
{
    private Long id;

    private Long methodId;

    private String commentedBy;

    private String commenterAvatarUrl;

    private String commenterEmail;

    private Long commenterId;

    private List<String> commenterBadges;

    private String message;

    private LocalDateTime commentedOn;

    private List<CommentItemResponse> subComments;

    public CommentItemResponse(Comment commentEntity){
        this.id = commentEntity.getId();
        this.methodId = commentEntity.getMethodId();
        this.message = commentEntity.getMessage();
        this.commenterBadges = commentEntity.getCommentedByUser().getBadgeNames();
        this.commentedBy = commentEntity.getCommentedByUser().getFullName();
        this.commenterEmail = commentEntity.getCommentedByUser().getEmail();
        this.commenterId = commentEntity.getCommentedByUser().getId();
        this.commentedOn = commentEntity.getCommentedOn();

        if(!CollectionUtils.isNullOrNoItem(commentEntity.getSubComments())){
            subComments = new ArrayList<>();
            for(var comment : commentEntity.getSubComments()){
                subComments.add(new CommentItemResponse(comment));
            }
        }
    }
}
