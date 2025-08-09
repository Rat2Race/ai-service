package org.rater.ai.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChunkingService {

    public List<String> splitToChunksBySentence(String userInput, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        StringBuilder chunk = new StringBuilder();

        for (String sentence : userInput.split("(?<=[.!?])\\s*")) {
            if (chunk.length() + sentence.length() > chunkSize) {
                chunks.add(chunk.toString());
                chunk = new StringBuilder();
            }
            chunk.append(sentence);
        }

        if (!chunk.isEmpty()) {
            chunks.add(chunk.toString());
        }

        return chunks;
    }

}
