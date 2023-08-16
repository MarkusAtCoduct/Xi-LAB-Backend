package com.codeleap.xilab.api.payload.response.method;

import com.codeleap.xilab.api.payload.response.data.ProcessPhaseItem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResponse {

    private Long id;

    private String owner;

    private List<ProcessPhaseItem> phases;
}
