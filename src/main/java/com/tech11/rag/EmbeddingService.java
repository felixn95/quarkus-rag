package com.tech11.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;

@ApplicationScoped
public class EmbeddingService {

	AzureOpenAiEmbeddingModel embeddingModel;

	@PostConstruct
	void init() {

		final String apiKey = System.getenv("AZURE_OPENAI_API_KEY_EMBEDDING_POLAND");
		final String endpoint = System.getenv("AZURE_OPENAI_ENDPOINT_POLAND");
		final String deployment = System.getenv("AZURE_OPENAI_EMBEDDING_DEPLOYMENT_NAME");

		embeddingModel = AzureOpenAiEmbeddingModel.builder()
				.apiKey(apiKey)
				.endpoint(endpoint)
				.deploymentName(deployment)
				.logRequestsAndResponses(true)
				.build();
	}

	public float[] createEmbeddingAzureOpenAI(String text) {
		// LangChain4J liefert ein Response-Objekt mit Embedding-Objekt
		Response<Embedding> response = embeddingModel.embed(text);
		Embedding embedding = response.content();

		// Embedding-Objekt liefert List<Float>. Hier wandeln wir in float[] um
		float[] vector = new float[embedding.vectorAsList().size()];
		for (int i = 0; i < embedding.vectorAsList().size(); i++) {
			vector[i] = embedding.vectorAsList().get(i);
		}
		return vector;
	}
}

