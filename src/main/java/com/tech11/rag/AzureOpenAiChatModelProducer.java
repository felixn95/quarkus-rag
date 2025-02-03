package com.tech11.rag;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class AzureOpenAiChatModelProducer {

	@Produces
	@ApplicationScoped
	public AzureOpenAiChatModel produceAzureOpenAiChatModel() {
		double temperature = 0.2;
		return AzureOpenAiChatModel.builder()
				.apiKey(System.getenv("AZURE_OPENAI_API_KEY_GERMANY"))
				.endpoint(System.getenv("AZURE_OPENAI_ENDPOINT_GERMANY"))
				.deploymentName(System.getenv("AZURE_OPENAI_CHAT_DEPLOYMENT_NAME"))
				.temperature(temperature)
				.logRequestsAndResponses(true)
				.build();
	}
}
