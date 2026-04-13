package org.messenger.messenger.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.messenger.messenger.messages.*;
import org.messenger.messenger.models.Message;
import org.messenger.messenger.models.User;

public class Server implements Runnable {
    private final int PORT = 8080;
    private final List<ConnectionHandler> allClients = new ArrayList<>();
    private final Map<String, ConnectionHandler> activeClients = new ConcurrentHashMap<>();
    private final UserService userService = new UserService();
    private final MessageService messageService = new MessageService();
    private Consumer<String> logConsumer;
    private volatile boolean running = true;
    private ServerSocket server;
    public Server(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
    }

    private void log(String msg) {
        if (logConsumer != null) {
            logConsumer.accept(msg);
        } else {
            System.out.println(msg);
        }
    }
    @Override
    public void run() {
        try {
            server = new ServerSocket(PORT);
            log("Сервер запущено на порті " + PORT);
            while (running) {
                try {
                    Socket client = server.accept();
                    ConnectionHandler handler = new ConnectionHandler(client, userService, messageService);
                    new Thread(handler).start();
                    allClients.add(handler);
                } catch (IOException e) {
                    if (running) {
                        log("Помилка під час підключення клієнта: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log("Помилка сервера: " + e.getMessage());
        } finally {
            try {
                if (server != null && !server.isClosed()) {
                    server.close();
                }
            } catch (IOException e) {
                log("Помилка при закритті сервера: " + e.getMessage());
            }
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (server != null && !server.isClosed()) {
                server.close();
            }
        } catch (IOException e) {
            log("Помилка при закритті сервера: " + e.getMessage());
        }
        Iterator<ConnectionHandler> i = allClients.iterator();
        while(i.hasNext()) {
            ConnectionHandler handler = i.next();
            handler.closeConnection();
            i.remove();
        }
    }

    class ConnectionHandler implements Runnable {
        private final Socket client;
        private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        private UserService userService;
        private MessageService messageService;
        private PrintWriter out;
        private String username;
        public ConnectionHandler(Socket client, UserService us, MessageService ms) {
            this.client = client;
            this.userService = us;
            this.messageService = ms;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void closeConnection() {
            try {
                if (client != null && !client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                log("Помилка при закритті клієнтського з'єднання: " + e.getMessage());
            }
        }


        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            ) {
                out = new PrintWriter(client.getOutputStream(), true);
                while (true) {
                    String input = in.readLine();

                    if (input == null) {
                        return;
                    }
                    JsonNode root = objectMapper.readTree(input);
                    String type = root.get("type").asText();
                    switch (type) {
                        case "login" -> {
                            String username = root.get("username").asText();
                            String password = root.get("password").asText();
                            User user = userService.findUserByUsername(username);
                            if (user == null || !PasswordsUtil.checkPassword(password, user.getPassword())) {
                                ServerMessage serverMessage = new ServerMessage("error", "Неправильний логін або пароль");
                                String jsonMessage = objectMapper.writeValueAsString(serverMessage);
                                out.println(jsonMessage);
                            } else {
                                ServerMessage serverMessage = new ServerMessage("success", "Вхід успішний");
                                log("Користувач " + username + " зайшов");
                                this.setUsername(username);
                                activeClients.put(username, this);
                                String jsonMessage = objectMapper.writeValueAsString(serverMessage);
                                out.println(jsonMessage);
                            }
                        }
                        case "registration" -> {
                            String username = root.get("username").asText();
                            String password = root.get("password").asText();
                            String repeatPassword = root.get("repeatPassword").asText();
                            ServerMessage serverMessage = new ServerMessage();
                            if (!password.equals(repeatPassword)) {
                                System.out.println("сценарій неуспіху (пароль той-же)");
                                serverMessage.setType("error");
                                serverMessage.setDetails("Паролі не співпадають");
                                String jsonMessage = objectMapper.writeValueAsString(serverMessage);
                                out.println(jsonMessage);
                                break;
                            }
                            User user = userService.findUserByUsername(username);
                            if (user != null) {
                                System.out.println("сценарій неуспіху (кор існує)");
                                serverMessage.setType("error");
                                serverMessage.setDetails("Користувач з таким ім'ям вже існує");
                                String jsonMessage = objectMapper.writeValueAsString(serverMessage);
                                out.println(jsonMessage);
                                return;
                            } else {
                                System.out.println("сценарій успіху");
                                serverMessage.setType("success");
                                serverMessage.setDetails("Реєстрація успішна");
                                log("Користувач " + username + " зареєструвався");
                                this.setUsername(username);
                                System.out.println(serverMessage);
                                String jsonMessage = objectMapper.writeValueAsString(serverMessage);
                                System.out.println(jsonMessage);
                                out.println(jsonMessage);
                                String hashedPassword = PasswordsUtil.hashPassword(password);
                                User newUser = new User(username, hashedPassword);
                                userService.saveUser(newUser);
                                NewUserMessage newUserMessage = new NewUserMessage(username);
                                String updateUsersJsonMessage = objectMapper.writeValueAsString(newUserMessage);
                                for (ConnectionHandler handler : activeClients.values()) {
                                    handler.out.println(updateUsersJsonMessage);
                                }
                                activeClients.put(username, this);
                            }

                        }
                        case "initializeUsers" -> {
                            String username = root.get("username").asText();
                            try {
                                List<String> usernames = userService.findAllUsernamesExceptCurrent(username);
                                ServerInitUsersMessage serverInitUsersMessage = new ServerInitUsersMessage();
                                serverInitUsersMessage.setUsernames(usernames);
                                String jsonMessage = objectMapper.writeValueAsString(serverInitUsersMessage);
                                out.println(jsonMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        case "getChatMessages" -> {
                            try {
                                String userFrom = root.get("userFrom").asText();
                                String userTo = root.get("userTo").asText();
                                List<Message> chatMessages = messageService.findMessagesBetweenUsers(userFrom, userTo);
                                ServerGetChatMessagesMessage serverGetChatMessagesMessage = new ServerGetChatMessagesMessage(chatMessages);
                                String jsonMessage = objectMapper.writeValueAsString(serverGetChatMessagesMessage);
                                System.out.println(jsonMessage);
                                out.println(jsonMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        case "sendMessage" -> {
                            String userFrom = root.get("userFrom").asText();
                            String userTo = root.get("userTo").asText();
                            String messageText = root.get("message").asText();
                            String timeString = root.get("time").asText();
                            log("Користувач " + userFrom + " відправив повідомлення \"" + messageText + "\" користувачу " + userTo
                                    + " о " + timeString);
                            LocalTime time = LocalTime.parse(timeString);
                            ConnectionHandler connectionHandlerUserTo = activeClients.get(userTo);
                            if(connectionHandlerUserTo != null) {
                                ServerIncomingMessage serverIncomingMessage = new ServerIncomingMessage(userFrom, messageText, time);
                                String json = objectMapper.writeValueAsString(serverIncomingMessage);
                                connectionHandlerUserTo.out.println(json);
                            }
                            Message message = new Message(userFrom, userTo, messageText, time);
                            messageService.saveMessage(message);
                        }
                    }
                }

            } catch (IOException e) {
                System.err.println("Помилка: " + e.getMessage());
            }
            finally {
                try {
                    if (username != null) {
                        activeClients.remove(username);
                        log("Коричтувач " + username + " від'єднався");
                    }
                    client.close();
                } catch (IOException e) {
                    System.err.println("Помилка при закритті з'єднання: " + e.getMessage());
                }
            }
        }
    }
}
