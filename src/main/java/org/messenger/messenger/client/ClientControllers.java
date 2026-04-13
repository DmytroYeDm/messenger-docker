package org.messenger.messenger.client;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.messenger.messenger.messages.LoginMessage;
import org.messenger.messenger.messages.RegisterMessage;


public class ClientControllers {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ClientConnection clientConnection;
    private SceneLoader sceneLoader = new SceneLoader();
    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    @FXML
    Button wantToRegisterButton;
    @FXML
    Button wantToLoginButton;
    @FXML
    Button registerButton;
    @FXML
    Button returnButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField registerUsernameField;
    @FXML
    private TextField registerPasswordField;
    @FXML
    private TextField repeatRegisterPasswordField;
    @FXML
    private Text errorText;
    @FXML
    private Text errorRegisterText;
    @FXML
    protected void onWantToRegisterButtonClick() {
        Stage currentStage = (Stage) wantToRegisterButton.getScene().getWindow();
        sceneLoader.loadScene("/org/messenger/messenger/client/authorization.fxml", currentStage, "Authorization",
                clientConnection);
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()) {
            errorText.setText("Не всі поля заповнені");
            return;
        }
        LoginMessage logMessage = new LoginMessage(username, password);


        new Thread(() ->{
            try {
                String jsonMessage = objectMapper.writeValueAsString(logMessage);
                clientConnection.sendMessage(jsonMessage);

                String response = clientConnection.readMessage();
                JsonNode root = objectMapper.readTree(response);
                String type = root.get("type").asText();

                Platform.runLater(() -> {
                    if (type.equals("success")) {
                        Stage stage = (Stage) wantToLoginButton.getScene().getWindow();
                        sceneLoader.loadMainScene("/org/messenger/messenger/client/mainPage.fxml", stage, username, clientConnection);
                    } else if (type.equals("error")) {
                        errorText.setText(root.get("details").asText());
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    wantToLoginButton.setDisable(true);
                    wantToRegisterButton.setDisable(true);
                    usernameField.setDisable(true);
                    passwordField.setDisable(true);
                    Stage stage = (Stage) wantToLoginButton.getScene().getWindow();
                    sceneLoader.loadProblemScene("/org/messenger/messenger/client/serverProblem.fxml", stage, "Problem");
                });
            }
        }).start();
    }

    @FXML
    protected void onRegisterButtonClick() {
        String username = registerUsernameField.getText();
        String password = registerPasswordField.getText();
        String repeatPassword = repeatRegisterPasswordField.getText();
        if(username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            errorRegisterText.setText("Не всі поля заповнені");
            return;
        }
        if(!password.equals(repeatPassword)) {
            errorRegisterText.setText("Паролі не співпадають");
            return;
        }
        RegisterMessage registerMessage = new RegisterMessage(username, password, repeatPassword);
        new Thread(() -> {
            try {
                String jsonMessage = objectMapper.writeValueAsString(registerMessage);
                clientConnection.sendMessage(jsonMessage);

                String response = clientConnection.readMessage();
                JsonNode root = objectMapper.readTree(response);
                String type = root.get("type").asText();

                Platform.runLater(() -> {
                    if (type.equals("success")) {
                        Stage stage = (Stage) registerButton.getScene().getWindow();
                        sceneLoader.loadMainScene("/org/messenger/messenger/client/mainPage.fxml", stage, username, clientConnection);
                    } else if (type.equals("error")) {
                        errorRegisterText.setText(root.get("details").asText());
                    }
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    registerButton.setDisable(true);
                    returnButton.setDisable(true);
                    registerUsernameField.setDisable(true);
                    registerPasswordField.setDisable(true);
                    repeatRegisterPasswordField.setDisable(true);
                    Stage stage = (Stage) registerButton.getScene().getWindow();
                    sceneLoader.loadProblemScene("/org/messenger/messenger/client/serverProblem.fxml", stage, "Problem");
                });
            }
        }).start();
    }

    @FXML
    protected void onReturnButtonClick() {
        Stage currentStage = (Stage) returnButton.getScene().getWindow();
        sceneLoader.loadScene("/org/messenger/messenger/client/login.fxml", currentStage, "Login",
                clientConnection);
    }
}
