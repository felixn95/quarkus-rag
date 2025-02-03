package com.tech11.rag;

import com.tech11.rag.dto.QueryRequestDTO;
import com.tech11.rag.dto.QueryResponseDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/query-documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QueryResource {

	@Inject
	QueryService queryService;

	@POST
	public Response processQuery(QueryRequestDTO request) {
		QueryResponseDTO response = queryService.processQuery(request);
		return Response.ok(response, MediaType.APPLICATION_JSON).build();
	}
}
