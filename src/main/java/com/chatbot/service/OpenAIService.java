package com.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Autowired
    private ChatModel chatModel;

    // Store conversation history
    private final List<Message> history = new ArrayList<>();

    public String getAnswerUsingOllama(String question) {
        // Add user message to history
        UserMessage userMessage = new UserMessage(question);
        history.add(userMessage);

        // Build prompt from history
        Prompt prompt = new Prompt(new ArrayList<>(history));
        ChatResponse response = chatModel.call(prompt);

        // Add assistant response to history
        AssistantMessage message = response.getResult().getOutput();
        history.add(message);

        return message.getText();
    }

    public String getAnswerFromOllamaUsingModelTemplate(String question) {
        // Load the prompt template from resources
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .resource(new ClassPathResource("ollama_template.json"))
                .build();

        // Define default values for placeholders
        Map<String, Object> defaultValues = new HashMap<>();
        defaultValues.put("modal_name", "DefaultModal");
        defaultValues.put("job_name", "DefaultJob");
        defaultValues.put("job_description", "No description provided");
        // ... add defaults for all placeholders

        // Create a prompt using the template and the question
        Prompt prompt = promptTemplate.create(defaultValues);
        prompt.augmentUserMessage(question);

        // Call the chat model with the constructed prompt
        ChatResponse response = chatModel.call(prompt);

        // Extract the assistant message from the response
        AssistantMessage message = response.getResult().getOutput();
        return message.getText();
    }

    public Map<String, Object> getAnswerUsingOllamaJSON(String question) {
        String expectedJsonFormat = "";
        try {
            ClassPathResource resource = new ClassPathResource("aggregrate.json");
            byte[] bytes = resource.getInputStream().readAllBytes();
            expectedJsonFormat = new String(bytes, StandardCharsets.UTF_8);
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
                .substring(message.getText().indexOf("{"), message.getText().lastIndexOf("}") + 1);
        System.out.println("LLM Response: " + messageText);
        try {
            return new ObjectMapper().readValue(messageText, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse LLM response as JSON: " + message, e);
        }
    }

    /*public String getAnswerUsingOpenAI(String question){
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        ResponseCreateParams createParams = ResponseCreateParams.builder()
                .input("Tell me a story about building the best SDK!")
                .model(ChatModel.GPT_4O)
                .build();

        client.responses().create(createParams).output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .forEach(outputText -> System.out.println(outputText.text()));
        return "Response received";
    }*/

}
