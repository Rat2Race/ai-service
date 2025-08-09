package org.rater.ai.service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TextPreprocessService {

    @Value("classpath:data/stopwords.txt")
    private Resource stopwordsResource;

    private Set<String> stopWords;

    @PostConstruct
    private void loadStopWords() {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(stopwordsResource.getInputStream()))) {
            stopWords = reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .collect(Collectors.toSet());
        } catch (Exception e) {
            stopWords = Set.of();
        }
    }

    public String removeStopWords(String userInput) {
        if (stopWords == null || stopWords.isEmpty()) {
            return userInput;
        }
        return Arrays.stream(userInput.split("\\s+"))
            .filter(word -> !stopWords.contains(word.toLowerCase()))
            .collect(Collectors.joining(" "));
    }

    public String preprocess(String userInput) {
        log.info("userInput:{}", userInput);
        String filteredText = removeSpecialChar(userInput);
        filteredText = normalizeWhitespace(filteredText);
        return filteredText;
    }

    private String removeSpecialChar(String text) {
        return text.replaceAll("[^\\p{L}\\p{N}\\s.?!]", "");
    }

    private String normalizeWhitespace(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }
}
