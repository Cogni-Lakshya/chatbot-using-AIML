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

    @GetMapping("/askOpenAI/{question}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String askQuestion(@PathVariable("question") String question){
        question = question.replaceAll("_", " ");
        return openAIService.getAnswer(question);
    }

    @GetMapping("/askOpenAI")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String askQuestion(){
        return openAIService.getAnswerUsingOpenAI(null);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String healthCheck(){
        return "Healthy";
    }

}
