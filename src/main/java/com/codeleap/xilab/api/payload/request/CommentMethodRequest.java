package com.codeleap.xilab.api.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentMethodRequest {
	private Long parentCommentId;

	@NotBlank(message = "Message should not be empty")
    @Size(min = 5, max = 2000, message = "Message length should be in [5, 2000]")
	private String message;
}