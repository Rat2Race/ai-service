package org.rater.ai.config;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingModelConfig {

    @Bean
    public OpenAiEmbeddingOptions embeddingOptions() {
        return OpenAiEmbeddingOptions.builder()
            .model("text-embedding-ada-002")
            .user("user-6")
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
