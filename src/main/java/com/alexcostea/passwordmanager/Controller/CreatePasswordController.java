package com.alexcostea.passwordmanager.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class CreatePasswordController {

    private final Stage mainStage;

    private final FXMLLoader newSceneLoader;


    @FXML
    protected PasswordField password;

    @FXML
    protected PasswordField confirmPassword;

    @FXML
    protected Label passwordMessage;

    @FXML
    protected Label passwordRequirements;

    @FXML
    protected Button confirmButton;

    public CreatePasswordController(Stage mainStage, FXMLLoader loader) {
        this.mainStage = mainStage;
        this.newSceneLoader = loader;
    }
    @FXML
    public void confirmPassword() {
        if(!this.password.getText().equals(this.confirmPassword.getText())) {
            this.password.getStyleClass().remove("weak_password");
            this.confirmPassword.getStyleClass().remove("weak_password");
            this.passwordRequirements.setText("");

            this.passwordMessage.setText("Passwords do not match!");
            this.password.getStyleClass().add("password_mismatch");
            this.confirmPassword.getStyleClass().add("password_mismatch");
        }
        else {
            this.password.getStyleClass().remove("password_mismatch");
            this.confirmPassword.getStyleClass().remove("password_mismatch");
            this.passwordMessage.setText("");

            if(!secure(this.password.getText())) {
                this.passwordMessage.setText("Password is not secure!");
                this.passwordRequirements.setText("""
                        A password must be at least 8 characters long,
                        include a lowercase letter, an uppercase letter,
                        a number and a special character""");
                this.password.getStyleClass().add("weak_password");
                this.confirmPassword.getStyleClass().add("weak_password");
            }
            else {
                try {
                    Scene newScene = new Scene(this.newSceneLoader.load(), 500, 500);
                    this.mainStage.setScene(newScene);
                } catch(IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    boolean secure(String password) {
        Pattern lowercase = Pattern.compile("[a-z]");
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern number = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile("[\\W_]");
        return  password.length() >= 8
                && lowercase.matcher(password).find()
                && uppercase.matcher(password).find()
                && number.matcher(password).find()
                && special.matcher(password).find();
    }
}
