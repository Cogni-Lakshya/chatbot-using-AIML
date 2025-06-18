package com.chatbot.controller;

import com.chatbot.service.OpenAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;

    @GetMapping("/ask_ollama/{question}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getAnswerUsingOllama(@PathVariable("question") String question){
        return openAIService.getAnswerUsingOllama(question);
    }

    @GetMapping("/ask_ollama_json/{question}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map getAnswerUsingOllamaJSON(@PathVariable("question") String question){
        Map response = openAIService.getAnswerUsingOllamaJSON(question);
        return response;
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
