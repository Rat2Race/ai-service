package org.rater.ai.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final PromptService promptService;
    private final TextPreprocessService textPreprocessService;
    private final EmbeddingService embeddingService;
    private final ChunkingService chunkingService;

    public ChatResponse getChatResponse(String userInput) {
        String preprocessed = preprocessInput(userInput);
        log.info("preprocessed: {}", preprocessed);

        List<String> chunks = chunkInput(preprocessed);
        log.info("chunks: {}", chunks);

        String context = buildContextFromChunks(chunks);
        log.info("context: {}", context);

        String promptInput = buildPromptInput(context, preprocessed);
        log.info("promptInput: {}", promptInput);

        Prompt prompt = promptService.createPrompt(promptInput);
        log.info("prompt: {}", prompt);

        return chatClient.prompt(prompt).call().chatResponse();
    }

    private String preprocessInput(String userInput) {
        return textPreprocessService.preprocess(userInput);
    }

    private List<String> chunkInput(String userInput) {
        return chunkingService.splitToChunksBySentence(userInput, 1024);
    }

    private String buildContextFromChunks(List<String> chunks) {
        StringBuilder sb = new StringBuilder();
        for (String chunk : chunks) {
            String cleaned = textPreprocessService.removeStopWords(chunk);
            String chunkContext = embeddingService.search(cleaned);
            if (!chunkContext.isBlank()) {
                sb.append(chunkContext).append("\n");
            }
        }
        return sb.toString();
    }

    private String buildPromptInput(String context, String question) {
        return context.isBlank() ? question : context + "\n---\n질문: " + question;
    }
}
