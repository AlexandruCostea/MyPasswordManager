package com.alexcostea.passwordmanager.Controller;

import com.alexcostea.passwordmanager.Controller.MainPageController;
import com.alexcostea.passwordmanager.Domain.Login;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AuthenticationController {

    private final Stage mainStage;
    private final FXMLLoader newSceneLoader;
    private final FXMLLoader addLoader;
    private final String css;

    @FXML
    protected PasswordField password;

    @FXML
    protected Label passwordMessage;

    private JsonData jsonData;

    public AuthenticationController(Stage stage, FXMLLoader loader, FXMLLoader addLoader, String css) {
        this.mainStage = stage;
        this.newSceneLoader = loader;
        this.addLoader = addLoader;
        this.css = css;
    }

    public void initialize() {
        Path path = Paths.get("data/data.json");
        try {
            String data = new String(Files.readAllBytes(path));
            this.jsonData = new Gson().fromJson(data, JsonData.class);
        } catch(Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }

    @FXML
    protected void tryAuthentication() {
        String password = this.password.getText() + this.jsonData.salt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedInput = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String stringHash = new BigInteger(1, hashedInput).toString(16);

            if(!stringHash.equals(this.jsonData.hashedPassword))
                this.passwordMessage.setText("Incorrect password!");
            else {
                try {
                    if(this.jsonData.encryptedData.isEmpty())
                        this.newSceneLoader.setControllerFactory(param -> new MainPageController(this.addLoader, this.css));
                    else {
                        String jsonObjectList = decode(this.password.getText(), this.jsonData.iv, this.jsonData.encryptedData);
                        EntryList data = new Gson().fromJson(jsonObjectList, EntryList.class);
                        List<Login> logins = new ArrayList<>();
                        for(ListEntry entry: data.logins) {
                            logins.add(new Login(entry.title, entry.mailOrUsername, entry.password));
                        }
                        this.newSceneLoader.setControllerFactory(param -> new MainPageController(this.addLoader, this.css, logins));
                    }
                    Scene newScene = new Scene(this.newSceneLoader.load(), 500, 500);
                    newScene.getStylesheets().add(this.css);
                    this.mainStage.setScene(newScene);
                } catch(Exception e) {
                    System.out.println(e.getClass() + e.getMessage());
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
    }

    public String decode(String password, String iv, String encryptedData) throws Exception{
        byte[] decodedPassword = Base64.getDecoder().decode(password);
        byte[] decodedIv = Base64.getDecoder().decode(iv);
        byte[] decodedMessage = Base64.getDecoder().decode(encryptedData);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES");
        SecretKey secretKey = keyFactory.generateSecret(new SecretKeySpec(decodedPassword, "AES"));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(decodedIv));
        byte[] decryptedBytes = cipher.doFinal(decodedMessage);

        return new String(decryptedBytes);
    }

    public static class JsonData {
        public String salt;
        public String hashedPassword;

        public String iv;

        public String encryptedData;

    }

    public static class EntryList {
        public List<ListEntry> logins;
    }
    public static class ListEntry {
        public String title;

        public String mailOrUsername;

        public String password;
    }
}