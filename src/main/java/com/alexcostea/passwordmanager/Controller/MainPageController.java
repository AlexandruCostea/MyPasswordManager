package com.alexcostea.passwordmanager.Controller;

import com.alexcostea.passwordmanager.Domain.Login;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainPageController {

    private final List<Login> logins;

    private Scene addScene;

    private final String css;

    @FXML
    protected ListView<Login> loginsView;

    public MainPageController(FXMLLoader addLoader, String css) {
        this.logins = new ArrayList<>();
        this.css = css;
        try {
            this.addScene = new Scene(addLoader.load(), 400, 300);
            this.addScene.getStylesheets().add(this.css);
        } catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }

    public MainPageController(FXMLLoader addLoader, String css, List<Login> logins) {
        this.logins = logins;
        this.css = css;
        try {
            this.addScene = new Scene(addLoader.load(), 400, 300);
            this.addScene.getStylesheets().add(this.css);
        } catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }

    public void initialize() {
        this.loginsView.setItems(FXCollections.observableList(this.logins));
    }

    public void addLogin(MouseEvent mouseEvent) {
        try {
            Stage stage = new Stage();
            stage.setScene(addScene);
            stage.show();
        }catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }
}
