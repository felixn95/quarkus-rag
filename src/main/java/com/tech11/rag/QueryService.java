package com.tech11.rag;

import com.tech11.rag.controller.QueryController;
import com.tech11.rag.controller.ChatController;
import com.tech11.rag.dto.QueryRequestDTO;
import com.tech11.rag.dto.QueryResponseDTO;
import com.tech11.rag.dto.ChatRequestDTO;
import com.tech11.rag.dto.ChatResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

@ApplicationScoped
public class QueryService {

	@Inject
	private QueryController queryController;

	@Inject
	private ChatController chatController;

	public QueryResponseDTO processQuery(final QueryRequestDTO request) {
		return queryController.processQuery(request);
	}

	public ChatResponseDTO processChat(@Valid ChatRequestDTO request) {
		return chatController.processChat(request);
	}
}
