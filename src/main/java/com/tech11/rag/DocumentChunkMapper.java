package com.tech11.rag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech11.rag.dto.DocumentChunkDTO;
import com.tech11.rag.dto.DocumentChunkMetadataDTO;
import com.tech11.rag.entity.DocumentChunkEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DocumentChunkMapper {

	// Use an ObjectMapper to convert JsonNode metadata to your DTO.
	private final ObjectMapper objectMapper = new ObjectMapper();

	public DocumentChunkDTO toDto(DocumentChunkEntity entity) {
		if (entity == null) {
			return null;
		}

		DocumentChunkDTO dto = new DocumentChunkDTO();
		dto.setDocumentName(entity.getDocumentName());
		dto.setGlobalIndex(entity.getGlobalIndex());
		dto.setChunkContent(entity.getContent());

		// Convert JsonNode metadata to DocumentChunkMetadataDTO if present.
		if (entity.getMetadata() != null) {
			try {
				DocumentChunkMetadataDTO metadataDTO = objectMapper.treeToValue(entity.getMetadata(), DocumentChunkMetadataDTO.class);
				dto.setMetadata(metadataDTO);
			} catch (Exception e) {
				// Handle or log the exception as needed.
				e.printStackTrace();
			}
		}
		return dto;
	}

	public List<DocumentChunkDTO> toDto(List<DocumentChunkEntity> entities) {
		return entities.stream()
				.map(this::toDto)
				.collect(Collectors.toList());
	}
}
