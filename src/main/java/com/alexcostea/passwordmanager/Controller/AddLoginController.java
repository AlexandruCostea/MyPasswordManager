package com.alexcostea.passwordmanager.Controller;

import com.alexcostea.passwordmanager.Domain.Login;
import com.alexcostea.passwordmanager.Exceptions.RepositoryException;
import com.alexcostea.passwordmanager.Service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.security.SecureRandom;

public class AddLoginController {

    private final Service service;

    private ObservableList<Login> logins;

    private final ListView<Login> view;

    @FXML
    protected Label invalidLogin;

    @FXML
    protected TextField password;

    @FXML
    protected TextField mailOrUser;

    @FXML
    protected TextField title;

    @FXML
    protected Button randomPassword;

    public AddLoginController(Service service, ObservableList<Login> logins, ListView<Login> view) {
        this.service = service;
        this.logins = logins;
        this.view = view;
    }

    public void initialize() {
        this.password.setOnKeyPressed(this::checkKey);
        this.mailOrUser.setOnKeyPressed(this::checkKey);
        this.title.setOnKeyPressed(this::checkKey);
        this.randomPassword.setOnKeyPressed(this::checkKey);
    }

    private void checkKey(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            addLogin();
        }
    }

    public void addLogin() {
        Login login = new Login(this.title.getText(), this.mailOrUser.getText(), this.password.getText());
        try {
            this.service.add(login);
            this.invalidLogin.setText("");
            this.title.getStyleClass().remove("field_invalid");
            this.mailOrUser.getStyleClass().remove("field_invalid");
            this.password.getStyleClass().remove("field_invalid");
            this.title.setText("");
            this.mailOrUser.setText("");
            this.password.setText("");

            this.service.saveData();
            this.logins = FXCollections.observableList(this.service.getData());
            this.view.setItems(this.logins);

        } catch (RepositoryException e) {
            if(e.getMessage().equals("Empty mandatory fields"))
                this.invalidLogin.setText("Title and password can't be empty fields");
            else
                this.invalidLogin.setText("Login already exists");
            this.title.getStyleClass().add("field_invalid");
            this.mailOrUser.getStyleClass().add("field_invalid");
            this.password.getStyleClass().add("field_invalid");
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
