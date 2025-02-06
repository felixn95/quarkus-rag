package com.tech11.rag;

import com.tech11.rag.dto.ChatRequestDTO;
import com.tech11.rag.dto.ChatResponseDTO;
import com.tech11.rag.dto.QueryRequestDTO;
import com.tech11.rag.dto.QueryResponseDTO;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QueryResource {

	@Inject
	QueryService queryService;

	@POST
	@Path("/query-documents")
	public Response processQuery(@Valid final QueryRequestDTO request) {
		final QueryResponseDTO response = queryService.processQuery(request);
		return Response.ok(response, MediaType.APPLICATION_JSON).build();
	}

	// chat endpoint to try Azure JS example
	@POST
	@Path("/chat")
	public Response processChat(@Valid final ChatRequestDTO request) {
		final ChatResponseDTO response = queryService.processChat(request);
		return Response.ok(response, MediaType.APPLICATION_JSON).build();
	}
}
