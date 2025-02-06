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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class ChatController {

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

	public ChatResponseDTO processChat(final ChatRequestDTO request) {
		Objects.requireNonNull(request, "ChatRequestDTO must not be null");
		Objects.requireNonNull(request.getMessages(), "Messages list must not be null");

		final String question = extractLatestUserQuery(request.getMessages());
		if (question.isEmpty()) {
			throw new IllegalArgumentException("User query cannot be empty");
		}

		// Retrieve or create the conversation session
		final ConversationSession session = conversationSessionService.getSession(request.getSessionId());
		Objects.requireNonNull(session, "Conversation session must not be null");

		// TODO: For testing, hard code documentNames (enable in FE later!)
		request.setDocumentNames(List.of("phv-01_gdv"));

		// Prepare query context based on the current query and chat history
		final QueryContext queryContext = prepareQueryContext(question, session);

		// Perform vector search for similar document chunks
		final List<DocumentChunkEntity> similarChunks = vectorSearchService.searchByQuery(
				queryContext.getQueryForVectorSearch(),
				request.getDocumentNames(),
				DEFAULT_LIMIT,
				DEFAULT_METRIC
		);
		final List<DocumentChunkDTO> similarChunksDTOs = documentChunkMapper.toDto(similarChunks);

		// Build a prompt using the query, retrieved document chunks, and history summary.
		final String prompt = promptTemplateProvider.buildRagPrompt(
				question,
				similarChunksDTOs,
				queryContext.getHistorySummary(),
				PromptTemplateProvider.Language.GERMAN  // Adjust language as needed.
		);

		// Generate an answer using the Azure OpenAI model.
		final String answer = azureOpenAiChatModelProducer.produceAzureOpenAiChatModel().generate(prompt);

		// Update conversation with the new query and answer
		updateConversation(session, question, answer);
		conversationSessionService.saveSession(session);

		// Create a ChatThreadEntry for the assistant response
		final ChatThreadEntry entry = new ChatThreadEntry();
		entry.setId(UUID.randomUUID().toString());
		entry.setTimestamp(Instant.now().toString());
		entry.isUserMessage(false);

		final ChatMessageText messageText = new ChatMessageText();
		messageText.setValue(answer);
		messageText.setFollowingSteps(Collections.emptyList());
		entry.setText(Collections.singletonList(messageText));

		entry.setFollowupQuestions(Collections.emptyList());

		// Go through all chunks and add as citations
		final List<Citation> citations = new ArrayList<>();
		for (final DocumentChunkDTO chunk : similarChunksDTOs) {
			final Citation citation = new Citation();

			// Check if metadata exists to avoid NullPointerException
			DocumentChunkMetadataDTO metadata = chunk.getMetadata();
			String citationText = "";

			if (metadata != null) {
				String sourceHeading = metadata.getSourceHeading();
				String pageHeader = metadata.getPageHeader();

				// Apply priority: sourceHeading > pageHeader > ""
				citationText = sourceHeading != null ? sourceHeading : (pageHeader != null ? pageHeader : "");
			}

			citation.setText(citationText);
			citation.setId(chunk.getDocumentName());
			citations.add(citation);
		}

		entry.setCitations(citations);

		// Collect source headings as data points
		final List<String> dataPoints = new ArrayList<>();
		for (final DocumentChunkDTO chunk : similarChunksDTOs) {
			if (chunk.getMetadata() != null && chunk.getMetadata().getSourceHeading() != null) {
				dataPoints.add(chunk.getMetadata().getSourceHeading());
			}
		}
		entry.setDataPoints(dataPoints);
		entry.setThoughts(null);

		// Build and return the final ChatResponseDTO.
		final ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
		chatResponseDTO.setResponses(Collections.singletonList(entry));
		chatResponseDTO.setSessionState(Collections.singletonMap("sessionId", session.getSessionId()));

		return chatResponseDTO;
	}

	private String extractLatestUserQuery(final List<ChatMessage> messages) {
		for (int i = messages.size() - 1; i >= 0; i--) {
			final ChatMessage message = messages.get(i);
			if (message != null && "user".equalsIgnoreCase(message.getRole()) && message.getContent() != null) {
				return message.getContent();
			}
		}
		return "";
	}

	private QueryContext prepareQueryContext(final String originalQuery, final ConversationSession session) {
		Objects.requireNonNull(originalQuery, "Original query must not be null");
		Objects.requireNonNull(session, "Conversation session must not be null");

		final QueryContext queryContext = new QueryContext();
		if (session.getMessages() == null || session.getMessages().isEmpty()) {
			queryContext.setHistorySummary("");
			queryContext.setQueryForVectorSearch(originalQuery);
		} else {
			final String historySummary = historyCompressorService.compressHistory(session);
			final String optimizedQuery = historySummary.isBlank()
					? originalQuery
					: historyCompressorService.similaritySearchOptimizedQuery(historySummary, originalQuery);
			queryContext.setHistorySummary(historySummary);
			queryContext.setQueryForVectorSearch(optimizedQuery);
		}
		return queryContext;
	}

	private void updateConversation(final ConversationSession session, final String query, final String answer) {
		Objects.requireNonNull(session, "Conversation session must not be null");
		session.getMessages().add(new ChatMessage("user", query));
		session.getMessages().add(new ChatMessage("assistant", answer));
	}
}