package com.tech11.rag;

import com.tech11.rag.entity.ConversationSession;
import com.tech11.rag.util.PromptTemplateProvider;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.stream.Collectors;

@ApplicationScoped
public class HistoryCompressorService {

	@Inject
	AzureOpenAiChatModel azureOpenAiChatModel;
	@Inject
	PromptTemplateProvider promptTemplateProvider;

	/**
	 * Compresses the conversation history into a brief summary.
	 */
	public String compressHistory(ConversationSession session) {

		String history = extractedHistory(session);

		String compressPrompt = promptTemplateProvider.historySummaryPrompt(PromptTemplateProvider.Language.GERMAN, history);
		return azureOpenAiChatModel.generate(compressPrompt);
	}

	/**
	 * Extracts the conversation history into a single string.
	 */
	public String extractedHistory(ConversationSession session) {

		return session.getMessages().stream()
				.map(msg -> msg.getRole() + ": " + msg.getContent())
				.collect(Collectors.joining("\n"));
	}

	/**
	 * Generates a refined search query based on the conversation history summary and the current query.
	 */
	public String similaritySearchOptimizedQuery(String historySummary, String currentQuery) {
		String prompt = promptTemplateProvider.getSimilaritySearchPrompt(historySummary, currentQuery,
				PromptTemplateProvider.Language.GERMAN);
		return azureOpenAiChatModel.generate(prompt);
	}

}
