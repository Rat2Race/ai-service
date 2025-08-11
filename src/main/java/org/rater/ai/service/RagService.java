package org.rater.ai.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final TokenTextSplitter splitter;
    private final PromptService promptService;

    public void addDocument(String content, String source) {
        log.info("문서 추가 시작: {}", source);

        Document document = new Document(content);
        List<Document> documents = splitter.split(document);

        vectorStore.add(documents);

        log.info("{}개 청크를 벡터 스토어에 저장 완료", documents.size());
    }

    public String ask(String question) {
        log.debug("질문 받음: {}", question);

        Prompt prompt = promptService.createPrompt(question);

        String answer = chatClient.prompt(prompt)
            .user(question)
            .call()
            .content();

        log.debug("답변 생성 완료: {}", answer);
        return answer;
    }

}
