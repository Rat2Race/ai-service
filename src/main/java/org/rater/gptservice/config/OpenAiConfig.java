package org.rater.gptservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {
    private Logger logger = LoggerFactory.getLogger(OpenAiConfig.class);

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    @Bean
    public OpenAiApi openAiApi() {
        logger.debug("OpenAI API 클라이언트 초기화");
        return OpenAiApi.builder().apiKey(apiKey).build();
    }

}
