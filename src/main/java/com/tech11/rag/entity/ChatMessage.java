package com.tech11.rag.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {

	// role should be "user" or "assistant"
	private String role;
	private String content;

	public ChatMessage(String user, @NotBlank(message = "Query must not be blank") String query) {
	}
}
