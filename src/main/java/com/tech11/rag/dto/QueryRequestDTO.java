package com.tech11.rag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class QueryRequestDTO {

	// Optional sessionId; if null a new session is created.
	private String sessionId;

	@NotBlank(message = "Query must not be blank")
	private String query;

	List<String> documentNames;

}
