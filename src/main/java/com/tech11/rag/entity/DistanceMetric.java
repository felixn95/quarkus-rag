package com.tech11.rag.entity;

import lombok.Getter;

@Getter
public enum DistanceMetric {
	L2("l2_distance"),
	NEGATIVE_INNER_PRODUCT("negative_inner_product"),
	COSINE("cosine_distance"),
	L1("l1_distance"),
	HAMMING("hamming_distance"),
	JACCARD("jaccard_distance");

	private final String functionName;

	DistanceMetric(String functionName) {
		this.functionName = functionName;
	}

}
