package com.chatbot.controller;

import com.chatbot.service.OpenAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;


    @GetMapping(path = "/ask_ollama/{question}", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getAnswerUsingOllama(@PathVariable("question") String question) throws InterruptedException {
        Thread.sleep(1000); // Simulate delay for testing purposes
        return openAIService.getAnswerUsingOllama(question);
    }

    @GetMapping(path = "/ask_ollama_json/{question}", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map getAnswerUsingOllamaJSON(@PathVariable("question") String question){
        Map response = openAIService.getAnswerUsingOllamaJSON(question);
        return response;
    }

    @GetMapping(path = "/ask_ollama_json_file/{question}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getAnswerUsingOllamaJSONAsFile(@PathVariable("question") String question) throws IOException {
        Map response = openAIService.getAnswerUsingOllamaJSON(question);

        // Write JSON to temp file
        File tempFile = File.createTempFile("ollama_response_", ".json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, response);

        // Prepare file as resource
        FileSystemResource resource = new FileSystemResource(tempFile);

        // Set headers for download
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ollama_response.json");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // Build response and delete file after serving
        ResponseEntity<Resource> entity = ResponseEntity.ok()
                .headers(headers)
                .contentLength(tempFile.length())
                .body(resource);

        // Delete file after response (in a separate thread to avoid blocking)
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Wait for download to start
                tempFile.delete();
            } catch (InterruptedException ignored) {}
        }).start();

        return entity;
    }

//    @GetMapping("/ask_openai")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public String askQuestion(){
//        return openAIService.getAnswerUsingOpenAI(null);
//    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String healthCheck(){
        return "Healthy";
    }

}
