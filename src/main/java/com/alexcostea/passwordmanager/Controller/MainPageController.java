package com.alexcostea.passwordmanager.Controller;

import com.alexcostea.passwordmanager.Domain.Login;
import com.alexcostea.passwordmanager.Service.PasswordManagerService;
import com.alexcostea.passwordmanager.Service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.crypto.SecretKey;

public class MainPageController {

    private ObservableList<Login> logins;

    private final Service service;

    private Scene addScene;

    private final FXMLLoader viewLoader;

    private final String css;

    private ViewLoginController ctrl;

    private Scene viewScene;

    private final FXMLLoader addLoader;

    @FXML
    protected ListView<Login> loginsView;

    public MainPageController(FXMLLoader addLoader, FXMLLoader viewLoader, String css, Stage primaryStage,
                              SecretKey decryptKey, SecretKey encryptKey, String newSalt, String newHash) {
        this.viewLoader = viewLoader;
        this.css = css;
        this.service = new PasswordManagerService(decryptKey, encryptKey, newSalt, newHash);
        this.logins = FXCollections.observableList(this.service.getData());
        this.addLoader = addLoader;
        primaryStage.setOnCloseRequest(event -> this.service.saveData());
    }

    public void initialize() {
        this.logins = FXCollections.observableList(this.service.getData());
        this.loginsView.setItems(this.logins);
        try {
            addLoader.setControllerFactory(stage -> new AddLoginController(service, this.logins, this.loginsView));
            this.viewLoader.setControllerFactory(param -> new ViewLoginController(service, this.logins, null, this.css, this.loginsView));
            this.addScene = new Scene(addLoader.load(), 450, 300);
            this.addScene.getStylesheets().add(this.css);
            this.viewScene = new Scene(this.viewLoader.load(), 450, 300);
            this.viewScene.getStylesheets().add(this.css);
            this.ctrl = this.viewLoader.getController();
        } catch (Exception e) {
            System.out.println(e.getClass() + "\n" + e.getMessage());
        }
    }

    public void addLogin() {
        try {
            Stage stage = new Stage();
            stage.setScene(addScene);
            stage.show();
        }catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }

    public void viewLogin() {
        Login selectedLogin = this.loginsView.getSelectionModel().getSelectedItem();
        this.ctrl.setLogin(selectedLogin);
        try {
            Stage stage = new Stage();
            stage.setScene(this.viewScene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }

    }

    public void downloadPDF() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Password Security Warning");

        Label headerLabel = new Label("Important: Please read carefully");
        headerLabel.getStylesheets().add(this.css);
        headerLabel.getStyleClass().add("alert-header");
        alert.getDialogPane().setHeader(headerLabel);

        alert.setHeaderText("Important: Please read carefully");
        alert.setContentText("This PDF contains sensitive information. " +
                "It should be used for printing and kept in a safe place. " +
                "It should not be stored on your PC for a long period of time. " +
                "Do you wish to continue?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(this.css);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                this.service.downloadPDF();
            }
        });
    }

}
