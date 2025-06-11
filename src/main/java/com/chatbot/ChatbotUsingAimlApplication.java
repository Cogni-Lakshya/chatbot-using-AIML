package com.chatbot;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicBooleans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class ChatbotUsingAimlApplication {
	public static final String BOT_NAME = "alice"; // Name of your bot directory
	public static final String RESOURCE_PATH = "src/main/resources"; // Path to your resources folder

	public static void main(String[] args) {

		SpringApplication.run(ChatbotUsingAimlApplication.class, args);
		try {
			// Set trace mode for debugging (optional)
			MagicBooleans.trace_mode = false;

			// Initialize the bot
			Bot bot = new Bot(BOT_NAME, RESOURCE_PATH);
			Chat chatSession = new Chat(bot);

			Scanner scanner = new Scanner(System.in);
			System.out.println("Chatbot: Hello! How can I help you?");

			while (true) {
				System.out.print("You: ");
				String input = scanner.nextLine();

				if ("quit".equalsIgnoreCase(input)
						|| "exit".equalsIgnoreCase(input) || "end".equalsIgnoreCase(input)) {
					System.out.println("Chatbot: Goodbye!");
					break;
				}

				String response = chatSession.multisentenceRespond(input);
				System.out.println("Chatbot: " + response);
			}

			scanner.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
