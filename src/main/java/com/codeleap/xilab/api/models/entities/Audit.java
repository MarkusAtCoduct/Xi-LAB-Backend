package com.codeleap.xilab.api.models.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Embeddable
@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
public class Audit {

	@Column(name = "created_on")
	private LocalDateTime createdOn = LocalDateTime.now();

	@Column(name = "updated_on")
	private LocalDateTime updatedOn = LocalDateTime.now();

}
