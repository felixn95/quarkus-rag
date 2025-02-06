package com.tech11.rag.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ChatMessageText {
	// The actual text to be displayed (can include HTML if needed)
	private String value;

	// Optional list of follow-up steps (or additional instructions)
	private List<String> followingSteps;
}
