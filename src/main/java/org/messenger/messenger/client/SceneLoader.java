package org.messenger.messenger.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneLoader {

    public SceneLoader() {
    }

    public void loadProblemScene(String fxmlPath, Stage currentStage, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneLoader.class.getResource(fxmlPath)));
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(title);
            currentStage.show();
        } catch (Exception e) {
            System.err.println("Помилка при завантаженні сторінки з проблемою: " + e.getMessage());
        }
    }
    public void loadScene(String fxmlPath, Stage stage, String title, ClientConnection clientConnection) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(param -> {
                ClientControllers controller = new ClientControllers();
                controller.setClientConnection(clientConnection);
                return controller;
            });
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            System.err.println("Помилка при завантаженні сторінки: " + e.getMessage());
        }
    }
    public void loadMainScene(String fxmlPath, Stage currentStage, String username, ClientConnection clientConnection) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(SceneLoader.class.getResource(fxmlPath)));

            loader.setControllerFactory(param -> {
                MessengerControllers messengerController = new MessengerControllers();
                messengerController.setUsername(username);
                messengerController.setClientConnection(clientConnection);
                return messengerController;
            });

            Parent root = loader.load();

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Messenger");
            currentStage.show();

        } catch (IOException e) {
            System.err.println("Помилка при завантаженні головної сторінки: " + e.getMessage());
        }
    }
}
