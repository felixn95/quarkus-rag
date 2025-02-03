package com.tech11.rag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentChunkMetadataDTO {

	@JsonProperty("pageNumber")
	private String pageNumber;

	@JsonProperty("pageHeader")
	private String pageHeader;

	@JsonProperty("sourceHeading")
	private String sourceHeading;

	@JsonProperty("chunkIndex")
	private String chunkIndex;
}
