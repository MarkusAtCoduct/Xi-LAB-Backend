package com.codeleap.xilab.api.payload.response.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMethodItem {

	private Long id;

	private String name;

	private Double rate;

	private Integer numberOfRate;
}
