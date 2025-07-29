package org.rater.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatClient chatClient;
    private final PromptService promptService;

    public ChatResponse getChatResponse(String userInput) {
        return chat(userInput).chatResponse();
    }

    public String getChatContent(String userInput) {
        return chat(userInput).content();
    }

    private CallResponseSpec chat(String userInput) {
        Prompt prompt = promptService.createPrompt(userInput);
        return chatClient.prompt(prompt).call();
    }
}
