package com.tech11.rag.util;

import com.tech11.rag.dto.DocumentChunkDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class PromptTemplateProvider {

	private static final String NO_HISTORY_EN = "No conversation history available.";
	private static final String NO_HISTORY_DE = "Keine Historie vorhanden.";

	public String getSimilaritySearchPrompt(String historySummary, String currentQuery, Language language) {
		String template = loadResource("/prompt_templates/en_vector_similarity_compressing_prompt.txt",
				"/prompt_templates/de_vector_similarity_compressing_prompt.txt", language);
		return template
				.replace("{historySummary}", Objects.toString(historySummary, ""))
				.replace("{currentQuery}", currentQuery);
	}

	public String historySummaryPrompt(Language language, String history) {
		String template = loadResource("/prompt_templates/en_vector_similarity_compressing_prompt.txt",
				"/prompt_templates/de_vector_similarity_compressing_prompt.txt", language);
		return template.replace("{history}", history);
	}

	/**
	 * Builds a full RAG prompt based on language-specific templates. It includes conversation history, contextual
	 * document excerpts, and the current query.
	 *
	 * @param query
	 * 		The current user query.
	 * @param similarChunks
	 * 		A list of similar document chunks.
	 * @param historySummary
	 * 		A summary of previous conversation (if any).
	 * @param language
	 * 		The language to use.
	 * @return The complete prompt.
	 */
	public String buildRagPrompt(String query, List<DocumentChunkDTO> similarChunks, String historySummary,
			Language language) {
		String template = loadResource("/prompt_templates/en_rag_prompt_template.txt",
				"/prompt_templates/de_rag_prompt_template.txt", language);

		String historyText = (historySummary == null || historySummary.isBlank())
				? (language == Language.GERMAN ? NO_HISTORY_DE : NO_HISTORY_EN)
				: (language == Language.GERMAN ? "Historie der Konversation:\n" : "Conversation History:\n")
						+ historySummary;

		String context = "";
		if (similarChunks != null && !similarChunks.isEmpty()) {
			String excerpts = similarChunks.stream()
					.map(chunk -> {
						String pageHeader = chunk.getMetadata() != null && chunk.getMetadata().getPageHeader() != null
								? chunk.getMetadata().getPageHeader()
								: "";
						String sourceHeading =
								chunk.getMetadata() != null && chunk.getMetadata().getSourceHeading() != null
										? chunk.getMetadata().getSourceHeading()
										: "";
						String documentName = chunk.getDocumentName() != null ? chunk.getDocumentName() : "";
						String globalIndex = chunk.getGlobalIndex() != null ? chunk.getGlobalIndex().toString() : "";
						String chunkContent = chunk.getChunkContent() != null ? chunk.getChunkContent() : "";

						return String.format("%s (Doc: %s, Index: %s, pageHeader: %s, sourceHeading: %s)",
								chunkContent, documentName, globalIndex, pageHeader, sourceHeading);
					})
					.collect(Collectors.joining("\n"));

			context = excerpts;
		}

		return template
				.replace("{history}", historyText)
				.replace("{context}", context)
				.replace("{query}", query);
	}

	private String loadResource(String englishPath, String germanPath, Language language) {
		String resourcePath = language == Language.GERMAN ? germanPath : englishPath;
		return PromptResourceLoader.loadResource(resourcePath);
	}

	public enum Language {
		ENGLISH, GERMAN
	}
}
