package org.rater.ai.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorstore;

    private static final int TOP_K = 10;
    private static final double SIMILARITY_THRESHOLD = 0.7;

    public String search(String userInput) {
        List<Document> relatedDocs = vectorstore.similaritySearch(
            SearchRequest.builder()
                .query(userInput)
                .topK(TOP_K)
                .similarityThreshold(SIMILARITY_THRESHOLD)
                .build()
        );

        StringBuilder sb = new StringBuilder();
        if (relatedDocs != null) {
            for (Document doc : relatedDocs) {
                sb.append(doc.getText()).append("\n");
            }
        }

        return sb.toString().trim();
    }

//    public void saveDocument(String doc) {//
//        for(String chunk : chunks) {
//            Embedding embedding = embeddingModel.embed(cleaned);
//            Document document = new Document(cleaned, embedding);
//            vectorstore.add(document);
//        }
//    }
}
