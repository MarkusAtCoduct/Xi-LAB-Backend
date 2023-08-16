package com.codeleap.xilab.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class StorageImageInfo {

	private String originalImageUrl;

	private String thumbnailImageUrl;

}