package com.codeleap.xilab.api.payload.request;

import com.codeleap.xilab.api.payload.response.data.ProcessPhaseItem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrEditMyProcessRequest {
    private String name;

    private List<ProcessPhaseItem> phaseItems;
}
