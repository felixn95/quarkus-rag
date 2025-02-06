package com.tech11.rag.util;

import com.tech11.rag.dto.DocumentChunkDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class PromptTemplateProvider {

	private static final String NO_HISTORY_EN = "No conversation history available.";
	private static final String NO_HISTORY_DE = "Erste Anfrage, keine Historie vorhanden.";

	/**
	 * Generates a similarity search prompt by replacing placeholders in the template.
	 *
	 * @param historySummary The summary of conversation history (can be null).
	 * @param currentQuery   The current query string.
	 * @param language       The language in which the prompt should be generated.
	 * @return A formatted similarity search prompt.
	 */
	public String getSimilaritySearchPrompt(final String historySummary, final String currentQuery, final Language language) {
		final String template = loadResource("/prompt_templates/en_vector_similarity_compressing_prompt.txt",
				"/prompt_templates/de_vector_similarity_compressing_prompt.txt", language);
		return template
				.replace("{historySummary}", Objects.toString(historySummary, ""))
				.replace("{currentQuery}", currentQuery);
	}

	/**
	 * Generates a history summary prompt.
	 *
	 * @param language The language of the prompt.
	 * @param history  The conversation history.
	 * @return A formatted history summary prompt.
	 */
	public String historySummaryPrompt(final Language language, final String history) {
		final String template = loadResource("/prompt_templates/en_vector_similarity_compressing_prompt.txt",
				"/prompt_templates/de_vector_similarity_compressing_prompt.txt", language);
		return template.replace("{history}", history);
	}

	/**
	 * Builds a full RAG (Retrieval-Augmented Generation) prompt.
	 * Includes conversation history, relevant document excerpts, and the current query.
	 *
	 * @param query          The current user query.
	 * @param similarChunks  A list of relevant document chunks.
	 * @param historySummary A summary of previous conversations (if available).
	 * @param language       The language in which the prompt should be generated.
	 * @return A fully formatted RAG prompt.
	 */
	public String buildRagPrompt(final String query, final List<DocumentChunkDTO> similarChunks, final String historySummary,
			final Language language) {
		final String template = loadResource("/prompt_templates/en_rag_prompt_template.txt",
				"/prompt_templates/de_rag_prompt_template.txt", language);

		final String historyText = Optional.ofNullable(historySummary)
				.filter(summary -> !summary.isBlank())
				.map(summary -> language == Language.GERMAN ? "Historie der Konversation:\n" + summary : "Conversation History:\n" + summary)
				.orElse(language == Language.GERMAN ? NO_HISTORY_DE : NO_HISTORY_EN);

		final String context = Optional.ofNullable(similarChunks)
				.filter(chunks -> !chunks.isEmpty())
				.map(chunks -> chunks.stream().map(chunk -> {
					final String pageHeader = Optional.ofNullable(chunk.getMetadata())
							.map(meta -> meta.getPageHeader())
							.orElse("");
					final String sourceHeading = Optional.ofNullable(chunk.getMetadata())
							.map(meta -> meta.getSourceHeading())
							.orElse("");
					final String documentName = Optional.ofNullable(chunk.getDocumentName()).orElse("");
					final String globalIndex = Optional.ofNullable(chunk.getGlobalIndex()).map(Object::toString).orElse("");
					final String pageNumber = Optional.ofNullable(chunk.getMetadata())
							.map(meta -> meta.getPageNumber())
							.map(Object::toString)
							.orElse("");
					final String chunkContent = Optional.ofNullable(chunk.getChunkContent()).orElse("");

					return String.format("%s (Content: %s, Page: %s, pageHeader: %s, sourceHeading: %s)",
							chunkContent, documentName, pageNumber, pageHeader, sourceHeading);
				}).collect(Collectors.joining("\n")))
				.orElse("");

		return template
				.replace("{history}", historyText)
				.replace("{context}", context)
				.replace("{query}", query);
	}

	/**
	 * Loads the appropriate language-specific template resource.
	 *
	 * @param englishPath The path to the English template.
	 * @param germanPath  The path to the German template.
	 * @param language    The selected language.
	 * @return The loaded template as a string.
	 */
	private String loadResource(final String englishPath, final String germanPath, final Language language) {
		final String resourcePath = language == Language.GERMAN ? germanPath : englishPath;
		return PromptResourceLoader.loadResource(resourcePath);
	}

	/**
	 * Enum representing the supported languages for prompts.
	 */
	public enum Language {
		ENGLISH, GERMAN
	}
}
