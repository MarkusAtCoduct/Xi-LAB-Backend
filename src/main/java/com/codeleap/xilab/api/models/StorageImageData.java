package com.codeleap.xilab.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class StorageImageData {

	private byte[] originalImage;

	private byte[] thumbnailImage;

}