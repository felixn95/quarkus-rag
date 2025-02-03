package com.tech11.rag;

import com.tech11.rag.dto.VectorSearchRequestDTO;
import com.tech11.rag.entity.DocumentChunkEntity;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/vector-search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VectorSearchResource {

	@Inject
	VectorSearchService vectorSearchService;

	/**
	 * POST endpoint to perform vector search.
	 * Expects a JSON body matching VectorSearchRequestDTO.
	 */
	@POST
	public Response searchByQuery(@Valid VectorSearchRequestDTO request) {
		List<DocumentChunkEntity> results = vectorSearchService.searchByQuery(
				request.getQuery(),
				request.getDocumentNames(),
				request.getLimit(),
				request.getMetric());
		return Response.ok(results).build();
	}

	/**
	 * GET endpoint to retrieve a document chunk by document name and global index.
	 * Expects query parameters: documentName and globalIndex.
	 */
	@GET
	@Path("/chunk")
	public Response getChunk(@QueryParam("documentName") String documentName,
			@QueryParam("globalIndex") int globalIndex) {
		DocumentChunkEntity chunk = vectorSearchService.getChunkByDocumentNameAndGlobalIndex(documentName, globalIndex);
		return Response.ok(chunk).build();
	}
}
