package org.messenger.messenger.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader;
        Parent root;
        try {

            loader = new FXMLLoader(ClientApplication.class.getResource("/org/messenger/messenger/client/login.fxml"));
            ClientConnection clientConnection = new ClientConnection();

            loader.setControllerFactory(param -> {
                ClientControllers controller = new ClientControllers();
                controller.setClientConnection(clientConnection);
                return controller;
            });
            root = loader.load();
            stage.setTitle("Login");
        } catch (IOException e) {
            loader = new FXMLLoader(ClientApplication.class.getResource("/org/messenger/messenger/client/serverProblem.fxml"));
            root = loader.load();
            stage.setTitle("Error");
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
