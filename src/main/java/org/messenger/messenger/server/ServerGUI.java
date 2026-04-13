package org.messenger.messenger.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerGUI extends Application {

    private TextArea logArea;

    @Override
    public void start(Stage stage) throws IOException {
        logArea = new TextArea();
        logArea.setEditable(false);
        Scene scene = new Scene(logArea, 600, 400);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();

        Server server = new Server(this::logToUI);
        new Thread(server).start();

        stage.setOnCloseRequest(event -> {
            if (server != null) {
                server.stopServer();
            }
        });
    }

    private void logToUI(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public static void main(String[] args) {
        launch();
    }
}
