package com.alexcostea.passwordmanager;

import com.alexcostea.passwordmanager.Controller.CreatePasswordController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Path path = Paths.get("data/data.json");
        byte[] bytes = Files.readAllBytes(path);
        String jsonContent = new String(bytes);
        FXMLLoader fxmlLoader;
        if(jsonContent.isEmpty()) {
            fxmlLoader = new FXMLLoader(getClass().getResource("createPasswordMenu.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("authenticationMenu.fxml"));
            fxmlLoader.setControllerFactory(param -> new CreatePasswordController(stage, loader));
        }
        else
            fxmlLoader = new FXMLLoader(getClass().getResource("authenticationMenu.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        URL url = getClass().getResource("styles.css");
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }

        String css = url.toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("MyPasswordManager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}