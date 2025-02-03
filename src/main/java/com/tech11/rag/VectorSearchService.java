package com.tech11.rag;

import com.tech11.rag.entity.DistanceMetric;
import com.tech11.rag.entity.DocumentChunkEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class VectorSearchService {

	@Inject
	EntityManager entityManager;

	@Inject
	EmbeddingService embeddingService;

	/**
	 * Searches for the best matching document chunks based on a query string.
	 * The query is embedded and then the chunks are filtered by document names and ordered by similarity,
	 * using the specified distance metric.
	 *
	 * @param query         the query string (e.g., a legal question)
	 * @param documentNames list of document names to filter on
	 * @param limit         maximum number of results to return
	 * @param metric        the distance metric to use (e.g., COSINE, L2, etc.)
	 * @return List of matching DocumentChunkEntity
	 */
	@Transactional
	public List<DocumentChunkEntity> searchByQuery(String query, List<String> documentNames, int limit, DistanceMetric metric) {
		// Compute the query embedding.
		float[] queryEmbedding = embeddingService.createEmbeddingAzureOpenAI(query);

		// Construct the JPQL query dynamically using the chosen distance metric.
		String jpql = "FROM DocumentChunkEntity d " +
				"WHERE d.documentName IN :docNames " +
				"ORDER BY " + metric.getFunctionName() + "(d.embedding, :embedding)";
		TypedQuery<DocumentChunkEntity> typedQuery = entityManager.createQuery(jpql, DocumentChunkEntity.class);
		typedQuery.setParameter("docNames", documentNames);
		typedQuery.setParameter("embedding", queryEmbedding);
		typedQuery.setMaxResults(limit);

		// to tdo

		return typedQuery.getResultList();
	}

	/**
	 * Retrieves a specific document chunk based on its document name and global index.
	 * This method is useful for directly fetching a single chunk— for example, if you have a chunk with globalIndex 1
	 * and want to retrieve the previous context at globalIndex 0.
	 *
	 * @param documentName the document name
	 * @param globalIndex  the global index of the desired chunk
	 * @return the matching DocumentChunkEntity
	 * @throws IllegalArgumentException if no matching chunk is found
	 */
	@Transactional
	public DocumentChunkEntity getChunkByDocumentNameAndGlobalIndex(String documentName, int globalIndex) {
		String jpql = "FROM DocumentChunkEntity d WHERE d.documentName = :docName AND d.globalIndex = :globalIndex";
		TypedQuery<DocumentChunkEntity> query = entityManager.createQuery(jpql, DocumentChunkEntity.class);
		query.setParameter("docName", documentName);
		query.setParameter("globalIndex", globalIndex);
		query.setMaxResults(1);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			throw new IllegalArgumentException("Chunk not found for document '" + documentName +
					"' and globalIndex " + globalIndex);
		}
	}
}
