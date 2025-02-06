package com.tech11.rag.dto;

import java.util.List;
import java.util.Map;

import com.tech11.rag.entity.ChatMessage;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRequestDTO {

	@NotNull
	private List<ChatMessage> messages;

	// Allow passing additional context
	private Map<String, Object> context;

	// Specify whether the response should be streamed
	private boolean stream;

	// Session identifier
	private String sessionId;

	// List of document names to filter vector search
	private List<String> documentNames;
}
