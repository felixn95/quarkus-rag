package com.tech11.rag.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ConversationSession {

	private String sessionId;
	private List<ChatMessage> messages = new ArrayList<>();

	public ConversationSession() {
		this.sessionId = UUID.randomUUID().toString();
	}

}
