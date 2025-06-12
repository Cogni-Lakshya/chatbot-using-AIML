package com.chatbot.controller;

import com.chatbot.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
