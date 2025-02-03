package com.tech11.rag.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QueryResponseDTO {

	private String sessionId;
	private String answer;

}
