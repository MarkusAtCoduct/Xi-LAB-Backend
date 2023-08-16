package com.codeleap.xilab.api.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchMethodRequest {

	private String label;

	private String sortBy;

	private Boolean includeMethods;

	private Boolean includeMethodSets;
}