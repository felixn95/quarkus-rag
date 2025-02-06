package com.tech11.rag.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ChatResponseDTO {
	// A list of chat entries – each entry holds message text(s) and additional info
	private List<ChatThreadEntry> responses;

	// Optional session state for maintaining context/history
	private Map<String, Object> sessionState;
}
