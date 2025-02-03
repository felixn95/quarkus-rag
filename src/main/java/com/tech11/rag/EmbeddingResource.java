package com.tech11.rag;

import com.tech11.rag.dto.EmbedRequestDTO;
import jakarta.validation.Valid;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/create-embedding")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmbeddingResource {

	@Inject
	EmbeddingService embeddingService;

	@POST
	public float[] createEmbeddingAzureOpenAI(@Valid EmbedRequestDTO request) {
		return embeddingService.createEmbeddingAzureOpenAI(request.getTextChunk());
	}
}
