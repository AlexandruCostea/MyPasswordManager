package com.alexcostea.passwordmanager.Controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.*;

public class AuthenticationController {

    private final Stage mainStage;
    private final FXMLLoader newSceneLoader;
    private final FXMLLoader addLoader;

    private final FXMLLoader viewLoader;
    private final String css;

    @FXML
    protected PasswordField password;

    @FXML
    protected Label passwordMessage;

    private JsonData jsonData;

    public AuthenticationController(Stage stage, FXMLLoader loader, FXMLLoader addLoader, FXMLLoader viewLoader, String css) {
        this.mainStage = stage;
        this.newSceneLoader = loader;
        this.addLoader = addLoader;
        this.viewLoader = viewLoader;
        this.css = css;
    }

    public void initialize() {
        this.password.setOnKeyPressed(this::checkKey);
        Path path = Paths.get("data/data.json");
        try {
            String dataString = new String(Files.readAllBytes(path));
            ObjectMapper mapper = new ObjectMapper();
            this.jsonData = mapper.readValue
                    (dataString, JsonData.class);
        } catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }

    private void checkKey(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
                tryAuthentication();
        }
    }

    @FXML
    protected void tryAuthentication() {
        String password = this.password.getText() + this.jsonData.salt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedInput = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String stringHash = new BigInteger(1, hashedInput).toString(16);

            byte[] inputArray = stringHash.getBytes(StandardCharsets.UTF_8);
            byte[] hashedArray = this.jsonData.hashedPassword.getBytes(StandardCharsets.UTF_8);

            if (!Arrays.equals(inputArray, hashedArray))
                this.passwordMessage.setText("Incorrect password!");
            else {
                try {
                    JsonData newData = updatePassword(this.password.getText());
                    SecretKey decryptKey = createSecretKey(this.password.getText(), this.jsonData.salt);
                    SecretKey encryptKey = createSecretKey(this.password.getText(), newData.salt);
                    this.newSceneLoader.setControllerFactory(param ->
                            new MainPageController(this.addLoader, this.viewLoader, this.css, this.mainStage,
                                    decryptKey, encryptKey, newData.salt, newData.hashedPassword));
                    Scene newScene = new Scene(this.newSceneLoader.load(), 500, 500);
                    newScene.getStylesheets().add(this.css);
                    this.mainStage.setScene(newScene);
                } catch (Exception e) {
                    System.out.println(e.getClass() + e.getMessage());
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
    }

    String generatePasswordSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return new BigInteger(1, salt).toString(16);
    }

    JsonData updatePassword(String password) throws Exception {
        String newSalt = generatePasswordSalt();
        String saltedPassword = password + newSalt;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedPassword = digest.digest(saltedPassword.getBytes());
        String passwordHash = new BigInteger(1, encodedPassword).toString(16);
        return new JsonData(newSalt, passwordHash);
    }

    SecretKey createSecretKey(String password, String salt) throws Exception {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(passwordChars, saltBytes, 10000, 256);
        SecretKey secretKey1 = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey1.getEncoded(), "AES");
    }

    public static class JsonData {

        public JsonData() {

        }
        public JsonData(String salt, String hash) {
            this.salt = salt;
            this.hashedPassword = hash;
            this.iv = null;
            this.encryptedData = null;
        }

        @JsonProperty("salt")
        private String salt;

        @JsonProperty("hashedPassword")
        private String hashedPassword;

        @JsonProperty("iv")
        private String iv;

        @JsonProperty("encryptedData")
        private String encryptedData;

        public String getSalt() {
            return this.salt;
        }

        public String getHashedPassword() {
            return this.hashedPassword;
        }

        public String getIv() {
            return this.iv;
        }

        public String getEncryptedData() {
            return this.encryptedData;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public void setHashedPassword(String hashedPassword) {
            this.hashedPassword = hashedPassword;
        }

        public void setIv(String iv) {
            this.iv = iv;
        }

        public void setEncryptedData(String encryptedData) {
            this.encryptedData = encryptedData;
        }
    }
}