package com.tech11.rag;

import com.tech11.rag.entity.DocumentChunkEntity;
import com.tech11.rag.dto.DocumentChunkDTO;
import com.tech11.rag.repository.DocumentChunkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import com.tech11.rag.EmbeddingService;

@ApplicationScoped
public class IngestingService {

	@Inject
	DocumentChunkRepository repository;

	@Inject
	EmbeddingService embeddingService;

	@Inject
	ObjectMapper objectMapper;

	@Transactional
	public void ingest(DocumentChunkDTO docChunk) {

		float[] embedding = embeddingService.createEmbeddingAzureOpenAI(docChunk.getChunkContent());

		// Build metadata JSON as a JsonNode.
		ObjectNode metadataNode = objectMapper.createObjectNode();
		metadataNode.put("documentName", docChunk.getDocumentName());
		metadataNode.put("pageNumber", docChunk.getMetadata().getPageNumber());
		metadataNode.put("pageHeader", docChunk.getMetadata().getPageHeader());
		metadataNode.put("sourceHeading", docChunk.getMetadata().getSourceHeading());
		metadataNode.put("chunkIndex", docChunk.getMetadata().getChunkIndex());
		if (docChunk.getGlobalIndex() != null) {
			metadataNode.put("globalIndex", docChunk.getGlobalIndex());
		} else {
			metadataNode.putNull("globalIndex");
		}

		DocumentChunkEntity entity = new DocumentChunkEntity();
		entity.setContent(docChunk.getChunkContent() != null ? docChunk.getChunkContent() : "");
		entity.setDocumentName(docChunk.getDocumentName());
		entity.setGlobalIndex(docChunk.getGlobalIndex());
		entity.setMetadata(metadataNode);
		entity.setEmbedding(embedding);

		repository.persist(entity);
	}

}
