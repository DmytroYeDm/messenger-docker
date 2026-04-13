package org.messenger.messenger.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.messenger.messenger.messages.InitializeUsersMessage;
import org.messenger.messenger.messages.SendMessageToUserMessage;
import org.messenger.messenger.messages.GetChatMessagesMessage;
import org.messenger.messenger.models.Message;
import org.messenger.messenger.models.UserView;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class MessengerControllers implements MessageHandler{
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SceneLoader sceneLoader = new SceneLoader();
    private ClientConnection clientConnection;
    private ObservableList<UserView> allUsers;
    private String username;
    private String userTo;
    @FXML
    private TextField userInput;
    @FXML
    private ListView<UserView> listOfUsers;
    @FXML
    private VBox chatPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox messageBox;
    public void initialize() {

        InitializeUsersMessage initializeUsersMessage = new InitializeUsersMessage(username);
        String jsonMessage = null;
        try {
            jsonMessage = objectMapper.writeValueAsString(initializeUsersMessage);
        } catch (JsonProcessingException e) {
            Stage stage = (Stage) userInput.getScene().getWindow();
            sceneLoader.loadProblemScene("/org/messenger/messenger/client/clientProblem.fxml", stage, "Problem");
            return;
        }
        try {
            clientConnection.sendMessage(jsonMessage);
            String response = clientConnection.readMessage();
            JsonNode root = objectMapper.readTree(response);
            List<String> usernames = objectMapper.convertValue(
                    root.get("usernames"), new TypeReference<List<String>>() {
                    }
            );
            allUsers = FXCollections.observableArrayList();
            for (String name : usernames) {

                allUsers.add(new UserView(name));
            }
            listOfUsers.setCellFactory(lv -> new ListCell<>() {
                private final ImageView imageView = new ImageView();
                private final Label label = new Label();
                private final HBox hbox = new HBox(10, imageView, label);
                {
                    imageView.setFitWidth(40);
                    imageView.setFitHeight(40);
                    label.setStyle("-fx-font-size: 16px;");
                }
                @Override
                protected void updateItem(UserView item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        imageView.setImage(item.getAvatar());
                        label.setText(item.getUsername());
                        setGraphic(hbox);
                    }
                }
            });
            FilteredList<UserView> filteredUsers = new FilteredList<>(allUsers, p -> true);
            listOfUsers.setItems(filteredUsers);
            listOfUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    showChatForUser(newVal);
                }
            });

            userInput.textProperty().addListener((observable, oldValue, newValue) -> {
                String filter = newValue.trim();
                if (filter.isEmpty()) {
                    filteredUsers.setPredicate(user -> true);
                } else {
                    filteredUsers.setPredicate(user -> user.getUsername().contains(filter));
                }
            });
            MessageListener listener = new MessageListener(clientConnection, this);
            Thread thread = new Thread(listener);
            thread.setDaemon(true);
            thread.start();
        } catch (Exception e) {
            Stage stage = (Stage) userInput.getScene().getWindow();
            sceneLoader.loadProblemScene("/org/messenger/messenger/client/serverProblem.fxml", stage, "Problem");
        }
    }

    private void showChatForUser(UserView user) {
        userTo = user.getUsername();
        messageBox = new VBox(5);
        messageBox.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 10;");
        scrollPane = new ScrollPane(messageBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        TextField inputField = new TextField();
        inputField.setPromptText("Введіть повідомлення...");
        Button sendButton = new Button("Відправити");

        HBox inputBox = new HBox(5, inputField, sendButton);
        inputBox.setPadding(new Insets(10));
        HBox.setHgrow(inputField, Priority.ALWAYS);

        BorderPane fullChat = new BorderPane();
        fullChat.setCenter(scrollPane);
        fullChat.setBottom(inputBox);

        GetChatMessagesMessage getChatMessagesMessage = new GetChatMessagesMessage(username, userTo);
        String json = null;
        try {
            json = objectMapper.writeValueAsString(getChatMessagesMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            clientConnection.sendMessage(json);
        } catch (IOException e) {
            Stage stage = (Stage) userInput.getScene().getWindow();
            sceneLoader.loadProblemScene("/org/messenger/messenger/client/serverProblem.fxml", stage, "Problem");
        }


        chatPane.getChildren().setAll(fullChat);
        VBox.setVgrow(fullChat, Priority.ALWAYS);
        sendButton.setOnAction(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                String message = inputField.getText();
                SendMessageToUserMessage sendMessageToUserMessage = new SendMessageToUserMessage(username, userTo,
                        message, getCurrentTime());
                new Thread(() -> {
                    try {
                        String jsonMessage = objectMapper.writeValueAsString(sendMessageToUserMessage);
                        clientConnection.sendMessage(jsonMessage);

                        Platform.runLater(() -> {
                            inputField.clear();

                            String userMessageTime = getCurrentTime();
                            VBox userBubble = new VBox(3);
                            userBubble.setAlignment(Pos.TOP_LEFT);

                            Label userMessage = new Label(text);
                            userMessage.setStyle("-fx-background-color: #D1F5D3; -fx-padding: 8; -fx-background-radius: 10;");
                            userMessage.setWrapText(true);
                            userMessage.setMaxWidth(scrollPane.getWidth() * 0.45);

                            Label userTime = new Label(userMessageTime);
                            userTime.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

                            userBubble.getChildren().addAll(userMessage, userTime);
                            HBox userBox = new HBox(userBubble);
                            userBox.setAlignment(Pos.CENTER_LEFT);
                            messageBox.getChildren().add(userBox);
                            scrollPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                                userMessage.setMaxWidth(newWidth.doubleValue() * 0.45);
                            });
                        });

                    } catch (IOException ex) {
                        Stage stage = (Stage) userInput.getScene().getWindow();
                        sceneLoader.loadProblemScene("/org/messenger/messenger/client/serverProblem.fxml", stage, "Problem");
                    }
                }).start();


            }
        });
        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendButton.fire();
            }
        });

    }

    public MessengerControllers() {

    }

    @Override
    public void showAllMessages(List<Message> chatMessages) {
        Platform.runLater(() -> {
            messageBox.getChildren().clear();

            for (Message msg : chatMessages) {
                VBox bubble = new VBox(3);
                Label messageLabel = new Label(msg.getMessage());
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(scrollPane.getWidth() * 0.45);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String formattedTime = msg.getTime().format(formatter);
                Label timeLabel = new Label(formattedTime);
                timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

                bubble.getChildren().addAll(messageLabel, timeLabel);

                HBox messageContainer = new HBox(bubble);

                if (msg.getUserFrom().equals(username)) {

                    bubble.setAlignment(Pos.TOP_LEFT);
                    messageLabel.setStyle("-fx-background-color: #D1F5D3; -fx-padding: 8; -fx-background-radius: 10;");
                    messageContainer.setAlignment(Pos.CENTER_LEFT);
                } else {

                    bubble.setAlignment(Pos.TOP_RIGHT);
                    messageLabel.setStyle("-fx-background-color: #F5D1D1; -fx-padding: 8; -fx-background-radius: 10;");
                    messageContainer.setAlignment(Pos.CENTER_RIGHT);
                }

                messageBox.getChildren().add(messageContainer);


                scrollPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                    messageLabel.setMaxWidth(newWidth.doubleValue() * 0.45);
                });
            }

            scrollPane.setVvalue(1.0);
        });
    }

    @Override
    public void onIncomingMessage(String from, String message, String time) {

        if (userTo.equals(from)) {
            Platform.runLater(() -> {
                VBox receivedBubble = new VBox(3);
                receivedBubble.setAlignment(Pos.TOP_RIGHT);

                Label receivedMessage = new Label(message);
                receivedMessage.setStyle("-fx-background-color: #F5D1D1; -fx-padding: 8; -fx-background-radius: 10;");
                receivedMessage.setWrapText(true);
                receivedMessage.setMaxWidth(scrollPane.getWidth() * 0.45);

                Label receivedTime = new Label(time);
                receivedTime.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

                receivedBubble.getChildren().addAll(receivedMessage, receivedTime);
                HBox receivedBox = new HBox(receivedBubble);
                receivedBox.setAlignment(Pos.CENTER_RIGHT);
                messageBox.getChildren().add(receivedBox);

                scrollPane.setVvalue(1.0);
            });
        }
    }

    @Override
    public void onNewUser(String username) {
        Platform.runLater(() -> {
            UserView newUser = new UserView(username);
            allUsers.add(newUser);
        });
    }

    private String getCurrentTime() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }
}
