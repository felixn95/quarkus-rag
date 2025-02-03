package com.tech11.rag.repository;

import com.tech11.rag.entity.DocumentChunkEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DocumentChunkRepository {

	@Inject
	EntityManager em;

	@Transactional
	public void persist(DocumentChunkEntity entity) {
		em.persist(entity);
	}
}
