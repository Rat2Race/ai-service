package org.rater.ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final OpenAiChatModel chatModel;

    @GetMapping("/ai")
    public ResponseEntity<String> generation() {
        String content = chatClient.prompt()
            .user("hi, chatgpt")
            .call()
            .content();
        return ResponseEntity.status(HttpStatus.OK).body(content);
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody String userInput) {
        ChatResponse chatResponse = chatClient
            .prompt("요약해: " + "hello, world")
            .call()
            .chatResponse();
        return ResponseEntity.status(HttpStatus.OK).body(chatResponse);
    }
}
