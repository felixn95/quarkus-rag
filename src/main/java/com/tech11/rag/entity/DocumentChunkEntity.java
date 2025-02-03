package com.tech11.rag.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "chunks")
public class DocumentChunkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Embedding column using Hibernate's vector support.
	@Column
	@JdbcTypeCode(SqlTypes.VECTOR)
	@Array(length = 3072)
	private float[] embedding;

	// Text content remains as TEXT.
	@Column(columnDefinition = "TEXT")
	private String content;

	// Dedicated column for documentName to support efficient filtering.
	@Column(name = "document_name")
	private String documentName;

	// Dedicated column for globalIndex to support efficient filtering.
	@Column(name = "global_index")
	private Integer globalIndex;

	// Store metadata as native JSONB.
	@Column(columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private JsonNode metadata;

}
