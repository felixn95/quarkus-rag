package com.tech11.rag.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ChatThreadEntry {
	// A unique identifier (can be generated on the BE)
	private String id;

	// One or more text messages with optional follow-up steps
	private List<ChatMessageText> text;

	// Additional follow-up questions suggested by the bot
	private List<String> followupQuestions;

	// Any citations associated with the response (used in the FE citation list)
	private List<Citation> citations;

	// Timestamp to display when the message was created
	private String timestamp;

	// Indicates if this entry is a user message or from the bot
	private boolean isUserMessage;

	// Optional “thought process” text for the response
	private String thoughts;

	// Any data points or context information
	private List<String> dataPoints;

	// Optional error information if something went wrong
	private ChatError error;

	public void isUserMessage(boolean b) {
		this.isUserMessage = b;
	}
}
