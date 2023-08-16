package com.codeleap.xilab.api.payload.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class RateMethodRequest {

    @NotBlank(message = "Headline should not be empty")
    @Size(min = 5, max = 500, message = "Headline length should be in [5, 2000]")
	private String headline;

	private String message;

	@NotNull(message = "Stars should not be null")
    @Min(value = 1, message = "Min star value is 1")
    @Max(value = 10, message = "Max star value is 10")
	private Short stars;
}