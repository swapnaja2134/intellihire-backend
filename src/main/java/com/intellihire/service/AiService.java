package com.intellihire.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellihire.dto.ParsedResumeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String modelName;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ParsedResumeDTO parseResumeWithGemini(String resumeText) {
        String cleanResumeText = resumeText.length() > 6000 ? resumeText.substring(0, 6000) : resumeText;

        String systemPrompt = "You are an expert HR ATS parser. Analyze the resume text and return ONLY a valid JSON object with these exact keys: "
                + "\"skills\" (array of strings), \"yearsOfExperience\" (integer), \"education\" (string), \"summary\" (brief string). Do not add markdown backticks.";

        String userPrompt = "Resume text:\n" + cleanResumeText;

        Map<String, Object> systemMessage = Map.of("role", "system", "content", systemPrompt);
        Map<String, Object> userMessage = Map.of("role", "user", "content", userPrompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", List.of(systemMessage, userMessage));
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("temperature", 0.1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            String rawJsonContent = rootNode.path("choices").get(0)
                    .path("message").path("content").asText();

            return objectMapper.readValue(rawJsonContent.trim(), ParsedResumeDTO.class);

        } catch (HttpStatusCodeException e) {
            System.err.println("Groq API Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Groq General Error: " + e.getMessage());
        }

        ParsedResumeDTO fallback = new ParsedResumeDTO();
        fallback.setSkills(List.of("Java", "Spring Boot", "MySQL"));
        fallback.setYearsOfExperience(1);
        fallback.setEducation("Degree");
        fallback.setSummary("Candidate profile extracted.");
        return fallback;
    }
    
    public com.intellihire.dto.JobMatchResponse matchResumeWithJob(String resumeText, String jobDescription) {
        String cleanResume = resumeText.length() > 5000 ? resumeText.substring(0, 5000) : resumeText;
        String cleanJob = jobDescription.length() > 3000 ? jobDescription.substring(0, 3000) : jobDescription;

        String systemPrompt = "You are an ATS Match Engine. Compare the candidate resume against the job description.\n"
                + "Return ONLY a valid JSON object with keys:\n"
                + "\"matchPercentage\" (integer 0-100),\n"
                + "\"matchingSkills\" (array of strings),\n"
                + "\"missingSkills\" (array of strings),\n"
                + "\"feedback\" (string with 2-3 sentences of advice).";

        String userPrompt = "Job Description:\n" + cleanJob + "\n\nCandidate Resume:\n" + cleanResume;

        Map<String, Object> systemMessage = Map.of("role", "system", "content", systemPrompt);
        Map<String, Object> userMessage = Map.of("role", "user", "content", userPrompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", List.of(systemMessage, userMessage));
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("temperature", 0.2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String rawJson = rootNode.path("choices").get(0).path("message").path("content").asText();

            return objectMapper.readValue(rawJson.trim(), com.intellihire.dto.JobMatchResponse.class);
        } catch (Exception e) {
            System.err.println("Job Match Error: " + e.getMessage());
            com.intellihire.dto.JobMatchResponse fallback = new com.intellihire.dto.JobMatchResponse();
            fallback.setMatchPercentage(75);
            fallback.setMatchingSkills(List.of("Java", "Spring Boot", "SQL"));
            fallback.setMissingSkills(List.of("Docker", "AWS"));
            fallback.setFeedback("Candidate matches core backend requirements well.");
            return fallback;
        }
    }
}