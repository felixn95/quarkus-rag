package com.tech11.rag;

import com.tech11.rag.IngestingService;
import com.tech11.rag.dto.DocumentChunkDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/ingest")
public class IngestingResource {

	@Inject
	IngestingService ingestService;

	/**
	 * Accepts a JSON array of DocumentChunk objects.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ingestChunks(List<DocumentChunkDTO> documentChunks) {
		if (documentChunks != null) {
			documentChunks.forEach(ingestService::ingest);
		}
		return Response.ok().build();
	}
}
