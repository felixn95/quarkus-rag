package com.tech11.rag.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class EmbedRequestDTO {

	@NotBlank(message = "Request text must not be blank")
	private String textChunk;

}
