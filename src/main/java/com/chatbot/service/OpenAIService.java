package com.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class OpenAIService{

    @Autowired
    private ChatModel chatModel;

    public String getAnswerUsingOllama(String question){
        PromptTemplate template = new PromptTemplate(question);
        Prompt prompt = template.create();
        ChatResponse response = chatModel.call(prompt);
        AssistantMessage message = response.getResult().getOutput();
        return message.getText();
    }

    public Map<String, Object> getAnswerUsingOllamaJSON(String question){
        String expectedJsonFormat = "";
        try {
            ClassPathResource resource = new ClassPathResource("aggregrate.json");
            byte[] bytes = resource.getInputStream().readAllBytes();
            expectedJsonFormat =  new String(bytes, StandardCharsets.UTF_8);
            System.out.println("Expected JSON format: " + expectedJsonFormat);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read aggregrate.json | ", e);
        }

        // Construct the prompt for Ollama
        String promptString = """
            Please analyze the following text and return a response in the exact JSON format below and update your explanation in place of <short summary> & <detailed summary> in json.

            JSON format:
            %s

            Text: %s
            """.formatted(expectedJsonFormat, question);
        ChatResponse response = chatModel.call(new Prompt(new UserMessage(promptString)));
        AssistantMessage message = response.getResult().getOutput();
        String messageText = message.getText()
                .substring(message.getText().indexOf("{"), message.getText().lastIndexOf("}")+1);
        System.out.println("LLM Response: " + messageText);
        try {
            return new ObjectMapper().readValue(messageText, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse LLM response as JSON: " + message, e);
        }
    }

//    public String getAnswerUsingOpenAI(String question){
//        OpenAIClient client = OpenAIOkHttpClient.fromEnv();
//
//        ResponseCreateParams createParams = ResponseCreateParams.builder()
//                .input("Tell me a story about building the best SDK!")
//                .model(ChatModel.GPT_4O)
//                .build();
//
//        client.responses().create(createParams).output().stream()
//                .flatMap(item -> item.message().stream())
//                .flatMap(message -> message.content().stream())
//                .flatMap(content -> content.outputText().stream())
//                .forEach(outputText -> System.out.println(outputText.text()));
//        return "Response received";
//    }

}
