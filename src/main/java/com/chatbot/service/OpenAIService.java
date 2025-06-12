package com.chatbot.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService{

    @Autowired
    private OpenAiChatModel chatModel;

    public String getAnswer(String question){
        PromptTemplate template = new PromptTemplate(question);
        Prompt prompt = template.create();
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().toString();
    }

    public String getAnswerUsingOpenAI(String question){
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
    }

}
