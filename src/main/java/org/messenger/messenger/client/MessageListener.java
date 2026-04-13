package org.messenger.messenger.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import org.messenger.messenger.models.Message;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessageListener implements Runnable{
    private final ClientConnection clientConnection;
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final MessageHandler handler;

    public MessageListener(ClientConnection clientConnection, MessageHandler handler) {
        this.clientConnection = clientConnection;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String json = clientConnection.readMessage();
                if (json == null) {
                    break;
                }
                JsonNode root = objectMapper.readTree(json);

                String type = root.get("type").asText();
                switch (type) {
                    case "incomingMessage" -> {
                        String from = root.get("userFrom").asText();
                        String message = root.get("messageText").asText();
                        String time = root.get("time").asText();
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
                        String formattedTime = LocalTime.parse(time, inputFormatter).format(outputFormatter);
                        Platform.runLater(() -> handler.onIncomingMessage(from, message, formattedTime));
                    }
                    case "newUserMessage" -> {
                        String newUsername = root.get("newUserUsername").asText();
                        Platform.runLater(() -> {
                            handler.onNewUser(newUsername);
                        });
                    }
                    case "sendChatMessages" -> {
                        JsonNode messagesNode = root.get("chatMessages");
                        List<Message> chatMessages = objectMapper.convertValue(
                                messagesNode,
                                new TypeReference<List<Message>>() {});
                        Platform.runLater(() -> handler.showAllMessages(chatMessages));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Помилка при створенні слухача: " + e.getMessage());
        }
    }
}
