package com.alexcostea.passwordmanager.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.regex.Pattern;

public class CreatePasswordController {

    private final Stage mainStage;

    private final FXMLLoader newSceneLoader;

    private final FXMLLoader addLoader;

    private final FXMLLoader viewLoader;
    private final String css;


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

    public CreatePasswordController(Stage mainStage, FXMLLoader loader, FXMLLoader addLoader, FXMLLoader viewLoader, String css) {
        this.mainStage = mainStage;
        this.newSceneLoader = loader;
        this.addLoader = addLoader;
        this.viewLoader = viewLoader;
        this.css = css;
    }

    public void initialize() {
        this.password.setOnKeyPressed(this::checkKey);
        this.confirmPassword.setOnKeyPressed(this::checkKey);
    }

    private void checkKey(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            confirmPassword();
        }
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
                String salt = generatePasswordSalt();
                String saltedPassword = this.password.getText() + salt;
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] encodedPassword = digest.digest(saltedPassword.getBytes());
                    String passwordHash = new BigInteger(1,encodedPassword).toString(16);
                    SecretKey key = createSecretKey(this.password.getText(), salt);
                    this.newSceneLoader.setControllerFactory(param ->
                            new MainPageController(this.addLoader, this.viewLoader, this.css, this.mainStage,
                                    key, key, salt, passwordHash));
                    Scene newScene = new Scene(this.newSceneLoader.load(), 500, 500);
                    newScene.getStylesheets().add(this.css);
                    this.mainStage.setScene(newScene);
                } catch(Exception e) {
                    System.out.println(e.getClass() + e.getMessage());
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

    String generatePasswordSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return new BigInteger(1,salt).toString(16);
    }

    SecretKey createSecretKey(String password, String salt) throws Exception {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(passwordChars, saltBytes, 10000, 256);
        SecretKey secretKey1 = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey1.getEncoded(), "AES");
    }
}
