package org.rater.ai.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rater.ai.dto.AnswerResponse;
import org.rater.ai.dto.MessageResponse;
import org.rater.ai.dto.QuestionRequest;
import org.rater.ai.dto.UploadTextRequest;
import org.rater.ai.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/rag")
@RestController
@Slf4j
public class RagController {

    private final RagService ragService;

    @PostMapping("/documents/text")
    public ResponseEntity<?> uploadText(@RequestBody UploadTextRequest request) {
        try {
            ragService.addDocument(request.content(), request.source());
            return ResponseEntity.ok(new MessageResponse("문서가 성공적으로 추가되었습니다."));
        } catch (Exception e) {
            log.error("문서 추가 실패", e);
            return ResponseEntity.internalServerError()
                .body(new MessageResponse("문서 추가 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/documents/file")
    public ResponseEntity<?> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam(name = "source", required = false) String source) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            String documentSource = source != null ? source : file.getOriginalFilename();

            ragService.addDocument(content, documentSource);
            return ResponseEntity.ok(new MessageResponse("파일이 성공적으로 업로드되었습니다."));
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.internalServerError()
                .body(new MessageResponse("파일 업로드 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody QuestionRequest request) {
        try {
            String answer = ragService.ask(request.question());
            return ResponseEntity.ok(new AnswerResponse(answer));
        } catch (Exception e) {
            log.error("질문 처리 실패", e);
            return ResponseEntity.internalServerError()
                .body(new MessageResponse("질문 처리 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(new MessageResponse("RAG 서비스가 정상 작동 중입니다."));
    }
}
