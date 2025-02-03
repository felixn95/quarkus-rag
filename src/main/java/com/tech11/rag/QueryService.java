package com.tech11.rag;

import com.tech11.rag.dto.DocumentChunkDTO;
import com.tech11.rag.dto.QueryRequestDTO;
import com.tech11.rag.dto.QueryResponseDTO;
import com.tech11.rag.entity.ChatMessage;
import com.tech11.rag.entity.ConversationSession;
import com.tech11.rag.entity.DocumentChunkEntity;
import com.tech11.rag.entity.DistanceMetric;
import com.tech11.rag.util.PromptTemplateProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class QueryService {

	// Default vector search parameters; these could be made configurable.
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

	/**
	 * Processes a user query by:
	 * <ul>
	 *     <li>Retrieving the conversation session.</li>
	 *     <li>Compressing conversation history (if available) and optimizing the query for vector search.</li>
	 *     <li>Performing a vector search to retrieve contextually similar document chunks.</li>
	 *     <li>Building a prompt using language-specific templates.</li>
	 *     <li>Generating an answer using the Azure OpenAI Chat Model.</li>
	 *     <li>Updating the conversation session with the query and answer.</li>
	 * </ul>
	 *
	 * @param request the user's query request containing the query, session ID, and document names.
	 * @return a response DTO containing the session ID and the generated answer.
	 */
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

		// TODO: The language is hard-coded as GERMAN; consider making this configurable later
		final String prompt = promptTemplateProvider.buildRagPrompt(
				request.getQuery(),
				similarChunksDTOs,
				queryContext.getHistorySummary(),
				PromptTemplateProvider.Language.GERMAN
		);

		final String answer = azureOpenAiChatModelProducer.produceAzureOpenAiChatModel().generate(prompt);

		addMessagesToSession(session, request.getQuery(), answer);
		conversationSessionService.saveSession(session);

		final QueryResponseDTO response = new QueryResponseDTO();
		response.setSessionId(session.getSessionId());
		response.setAnswer(answer);
		return response;
	}

	/**
	 * Prepares the query context by compressing the conversation history if available and
	 * optimizing the query for vector search.
	 *
	 * @param originalQuery the original user query.
	 * @param session       the current conversation session.
	 * @return a {@link QueryContext} containing the history summary and the query to be used for vector search.
	 */
	private QueryContext prepareQueryContext(final String originalQuery, final ConversationSession session) {
		if (session.getMessages().isEmpty()) {
			return new QueryContext("", originalQuery);
		}
		final String historySummary = historyCompressorService.compressHistory(session);
		final String optimizedQuery = historySummary.isBlank()
				? originalQuery
				: historyCompressorService.similaritySearchOptimizedQuery(historySummary, originalQuery);
		return new QueryContext(historySummary, optimizedQuery);
	}

	/**
	 * Adds the user query and assistant's answer as messages to the conversation session.
	 *
	 * @param session the conversation session to update.
	 * @param query   the user query.
	 * @param answer  the generated answer.
	 */
	private void addMessagesToSession(final ConversationSession session, final String query, final String answer) {
		session.getMessages().add(new ChatMessage("user", query));
		session.getMessages().add(new ChatMessage("assistant", answer));
	}

	/**
	 * Helper class to hold the prepared query context.
	 */
	private static class QueryContext {
		private final String historySummary;
		private final String queryForVectorSearch;

		public QueryContext(final String historySummary, final String queryForVectorSearch) {
			this.historySummary = historySummary;
			this.queryForVectorSearch = queryForVectorSearch;
		}

		public String getHistorySummary() {
			return historySummary;
		}

		public String getQueryForVectorSearch() {
			return queryForVectorSearch;
		}
	}
}
