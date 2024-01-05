package com.alexcostea.passwordmanager.Controller;

import com.alexcostea.passwordmanager.Domain.Login;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddLoginController {

    private final ObservableList<Login> logins;

    @FXML
    protected Label invalidLogin;

    public AddLoginController(ObservableList<Login> logins) {
        this.logins = logins;
    }

    @FXML
    protected TextField password;

    @FXML
    protected TextField mailOrUser;

    @FXML
    protected TextField title;

    public void addLogin() {
        Login login = new Login(this.title.getText(), this.mailOrUser.getText(), this.password.getText());
        if(this.logins.contains(login)) {
            this.invalidLogin.setText("Login already exists");
            this.title.getStyleClass().add("field_invalid");
            this.mailOrUser.getStyleClass().add("field_invalid");
            this.password.getStyleClass().add("field_invalid");
        }
        else {
            this.logins.add(login);
            this.invalidLogin.setText("");
            this.title.getStyleClass().remove("field_invalid");
            this.mailOrUser.getStyleClass().remove("field_invalid");
            this.password.getStyleClass().remove("field_invalid");
            this.title.setText("");
            this.mailOrUser.setText("");
            this.password.setText("");
        }
    }
}
