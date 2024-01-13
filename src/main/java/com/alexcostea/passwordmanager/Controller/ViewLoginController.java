package com.alexcostea.passwordmanager.Controller;

import com.alexcostea.passwordmanager.Domain.Login;
import com.alexcostea.passwordmanager.Exceptions.RepositoryException;
import com.alexcostea.passwordmanager.Service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.security.SecureRandom;

public class ViewLoginController {

    private ObservableList<Login> logins;
    private Login login;

    private final String css;

    private final Service service;
    private final ListView<Login> view;

    public ViewLoginController(Service service, ObservableList<Login> logins, Login login, String css, ListView<Login> view) {
        this.service = service;
        this.logins = logins;
        this.login = login;
        this.css = css;
        this.view = view;
    }

    public void setLogin(Login login) {
        this.login = login;
        this.title.setText(this.login.getTitle());
        this.mailOrUser.setText(this.login.getMailOrUsername());
        this.password.setText(this.login.getPassword());
    }

    @FXML
    public TextField password;

    @FXML
    protected TextField mailOrUser;

    @FXML
    protected TextField title;

    public void updateLogin() {
        Login login = new Login(this.title.getText(), this.mailOrUser.getText(), this.password.getText());
        if(!login.equals(this.login)) {
            try {
                this.service.addFirst(login);
                this.service.remove(this.login);
                this.setLogin(login);

                this.service.saveData();
                this.logins = FXCollections.observableList(this.service.getData());
                this.view.setItems(this.logins);

                this.title.getStyleClass().remove("field_invalid");
                this.mailOrUser.getStyleClass().remove("field_invalid");
                this.password.getStyleClass().remove("field_invalid");
            } catch (RepositoryException e) {
                this.title.getStyleClass().add("field_invalid");
                this.mailOrUser.getStyleClass().add("field_invalid");
                this.password.getStyleClass().add("field_invalid");
            }
        }
    }

    public void deleteLogin() {
        Login login = new Login(this.title.getText(), this.mailOrUser.getText(), this.password.getText());
        if(this.service.contains(login)) {
            Stage stage = new Stage();
            Label label = new Label("Are you sure?");
            Button button1 = new Button("Yes");
            button1.setOnMouseClicked((MouseEvent event) -> {
                if (this.service.contains(login)) {
                    this.service.remove(login);
                    this.service.saveData();
                    this.logins = FXCollections.observableList(this.service.getData());
                    this.view.setItems(this.logins);
                }
                stage.close();
            });
            Button button2 = new Button("No");
            button2.setOnMouseClicked((MouseEvent event) -> {
                stage.close();
            });
            HBox layout1 = new HBox();
            layout1.setAlignment(Pos.CENTER);
            layout1.setSpacing(20.0);
            layout1.getChildren().addAll(button1, button2);
            VBox layout2 = new VBox();
            layout2.setAlignment(Pos.CENTER);
            layout2.setSpacing(20.0);
            layout2.getChildren().addAll(label, layout1);
            Scene scene = new Scene(layout2, 200, 100);
            scene.getStylesheets().add(this.css);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void generatePassword() {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String symbols = "!.#@$%()!.#@$%()";

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder password = new StringBuilder();

        String allCharacters = lowercase + uppercase + digits + symbols;

        for (int i = 0; i < 15; i++) {
            int randomIndex = secureRandom.nextInt(allCharacters.length());
            char randomChar = allCharacters.charAt(randomIndex);
            password.append(randomChar);
        }

        this.password.setText(password.toString());
    }
}
