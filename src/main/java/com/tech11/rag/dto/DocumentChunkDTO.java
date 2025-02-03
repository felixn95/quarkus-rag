package com.tech11.rag.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentChunkDTO {

	@JsonProperty("documentName")
	private String documentName;

	@JsonProperty("globalIndex")
	private Integer globalIndex;

	// Maps the text content (i.e. `content` in the entity).
	@JsonProperty("chunkContent")
	private String chunkContent;

	// Nested metadata DTO for additional information.
	@JsonProperty("metadata")
	private DocumentChunkMetadataDTO metadata;
}
