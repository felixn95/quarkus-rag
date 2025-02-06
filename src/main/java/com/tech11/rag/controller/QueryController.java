package com.tech11.rag.controller;

import com.tech11.rag.dto.*;
import com.tech11.rag.entity.ChatMessage;
import com.tech11.rag.entity.ConversationSession;
import com.tech11.rag.entity.DocumentChunkEntity;
import com.tech11.rag.entity.DistanceMetric;
import com.tech11.rag.ConversationSessionService;
import com.tech11.rag.VectorSearchService;
import com.tech11.rag.HistoryCompressorService;
import com.tech11.rag.util.PromptTemplateProvider;
import com.tech11.rag.AzureOpenAiChatModelProducer;
import com.tech11.rag.util.QueryContext;
import com.tech11.rag.DocumentChunkMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class QueryController {

	private static final int DEFAULT_LIMIT = 7;
	private static final DistanceMetric DEFAULT_METRIC = DistanceMetric.COSINE;

	@Inject
	private VectorSearchService vectorSearchService;

	@Inject
	private DocumentChunkMapper documentChunkMapper;

	@Inject
	private ConversationSessionService conversationSessionService;

	@Inject
	private HistoryCompressorService historyCompressorService;

	@Inject
	private AzureOpenAiChatModelProducer azureOpenAiChatModelProducer;

	@Inject
	private PromptTemplateProvider promptTemplateProvider;

	public QueryResponseDTO processQuery(final QueryRequestDTO request) {
		final ConversationSession session = conversationSessionService.getSession(request.getSessionId());
		final QueryContext queryContext = prepareQueryContext(request.getQuery(), session);

		final List<DocumentChunkEntity> similarChunks = vectorSearchService.searchByQuery(
				queryContext.getQueryForVectorSearch(),
				request.getDocumentNames(),
				DEFAULT_LIMIT,
				DEFAULT_METRIC
		);
		final List<DocumentChunkDTO> similarChunksDTOs = documentChunkMapper.toDto(similarChunks);

		final String prompt = promptTemplateProvider.buildRagPrompt(
				request.getQuery(),
				similarChunksDTOs,
				queryContext.getHistorySummary(),
				PromptTemplateProvider.Language.GERMAN
		);

		final String answer = azureOpenAiChatModelProducer.produceAzureOpenAiChatModel().generate(prompt);

		updateConversation(session, request.getQuery(), answer);
		conversationSessionService.saveSession(session);

		final QueryResponseDTO response = new QueryResponseDTO();
		response.setSessionId(session.getSessionId());
		response.setAnswer(answer);

		return response;
	}

	private QueryContext prepareQueryContext(final String originalQuery, final ConversationSession session) {

		final QueryContext queryContext = new QueryContext();

		if (session.getMessages().isEmpty()) {
			queryContext.setHistorySummary("");
			queryContext.setQueryForVectorSearch(originalQuery);
			return queryContext;
		}
		final String historySummary = historyCompressorService.compressHistory(session);
		final String optimizedQuery = historySummary.isBlank()
				? originalQuery
				: historyCompressorService.similaritySearchOptimizedQuery(historySummary, originalQuery);
		queryContext.setHistorySummary(historySummary);
		queryContext.setQueryForVectorSearch(optimizedQuery);
		return queryContext;
	}

	private void updateConversation(final ConversationSession session, final String query, final String answer) {

		session.getMessages().add(new ChatMessage("user", query));
		session.getMessages().add(new ChatMessage("assistant", answer));
	}
}
