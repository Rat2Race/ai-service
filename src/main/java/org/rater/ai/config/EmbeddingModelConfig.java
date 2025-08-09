package org.rater.ai.config;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingModelConfig {

    @Value("${spring.ai.embedding.model}")
    private String model;

    @Value("${spring.ai.embedding.user}")
    private String user;

    @Bean
    public OpenAiEmbeddingOptions embeddingOptions() {
        return OpenAiEmbeddingOptions.builder()
            .model(model)
            .user(user)
            .build();
    }

    @Bean
    public OpenAiEmbeddingModel embeddingModel(OpenAiApi openAiApi, OpenAiEmbeddingOptions openAiEmbeddingOptions) {
        return new OpenAiEmbeddingModel(
            openAiApi,
            MetadataMode.EMBED,
            openAiEmbeddingOptions,
            RetryUtils.DEFAULT_RETRY_TEMPLATE
        );
    }
}
