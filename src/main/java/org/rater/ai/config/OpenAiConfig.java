package org.rater.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    // 나중에 환경변수로 설정
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    //
//    @Value("${spring.ai.openai.model}")
//    private String model;
//
//    @Value("${spring.ai.openai.temperature}")
//    private Double temperature;
//
//    @Value("${spring.ai.openai.maxTokens}")
//    private Integer maxTokens;
//
    @Bean
    public OpenAiApi openAiApi() {
        return OpenAiApi.builder()
            .apiKey(apiKey)
            .build();
    }

    @Bean
    public OpenAiEmbeddingModel embeddingModel(OpenAiApi openAiApi) {
        return new OpenAiEmbeddingModel(openAiApi);
    }

    @Bean
    public OpenAiChatModel chatModel(OpenAiApi openAiApi) {
        return OpenAiChatModel.builder()
            .openAiApi(openAiApi)
            .build();
    }
}
