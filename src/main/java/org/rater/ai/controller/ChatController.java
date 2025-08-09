package org.rater.ai.controller;

import lombok.RequiredArgsConstructor;
import org.rater.ai.service.ChatService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/ai")
@RestController
public class ChatController {

    private final ChatService chatService;

//    @GetMapping("/test")
//    public ResponseEntity<String> generation() {
//        return ResponseEntity.status(HttpStatus.OK)
//            .body(chatService.getChatContent("hello, world"));
//    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody String userInput) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(chatService.getChatResponse(userInput));
    }
}
