package com.tech11.rag.dto;

import com.tech11.rag.entity.DistanceMetric;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class VectorSearchRequestDTO {

	@NotBlank(message = "Query must not be blank")
	private String query;

	@NotEmpty(message = "Document names must not be empty")
	private List<String> documentNames;

	@Min(value = 1, message = "Limit must be at least 1")
	private int limit;

	@NotNull(message = "Metric must not be null")
	private DistanceMetric metric;

}
