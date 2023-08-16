package com.codeleap.xilab.api.payload.response.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentItem {

	private Long id;

	private String message;

	private String commentedBy;

	private List<CommentItem> thread;

}
