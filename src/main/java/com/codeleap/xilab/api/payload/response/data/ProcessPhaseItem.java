package com.codeleap.xilab.api.payload.response.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPhaseItem {

	private String phaseId;

	private MethodSetItem methodSet;

}
