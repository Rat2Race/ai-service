package org.rater.ai.service;

import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class PromptService {

    @Value("classpath:data/prompts/system-message.txt")
    private Resource systemMessage;

    @Value("classpath:data/prompts/user-message.txt")
    private Resource userMessage;

    private Message toUserMessage(String userInput) {
        PromptTemplate promptTemplate = new PromptTemplate(userMessage);
        return promptTemplate.createMessage(Map.of("userInput", userInput));
    }

    private Message toSystemMessage() {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemMessage);
        return systemPromptTemplate.createMessage();
    }

    public Prompt createPrompt(String userInput) {
        return new Prompt(List.of(toUserMessage(userInput), toSystemMessage()));
    }
}
