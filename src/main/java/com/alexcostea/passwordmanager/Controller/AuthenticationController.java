package com.alexcostea.passwordmanager.Controller;

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
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

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
                        this.newSceneLoader.setControllerFactory(param -> new MainPageController(this.addLoader, this.viewLoader, this.css, this.mainStage, this.password.getText()));
                    else {
                        String jsonObjectList = decode(this.password.getText(), this.jsonData.salt, this.jsonData.iv, this.jsonData.encryptedData);
                        EntryList data = new Gson().fromJson(jsonObjectList, EntryList.class);
                        data.logins.removeIf(Objects::isNull);
                        List<Login> logins = new ArrayList<>();
                        for(ListEntry entry: data.logins) {
                            logins.add(new Login(entry.title, entry.mailOrUsername, entry.password));
                        }
                        this.newSceneLoader.setControllerFactory(param -> new MainPageController(this.addLoader,this.viewLoader,  this.css, this.mainStage,this.password.getText(), logins));
                    }
                    updatePassword(jsonData, this.password.getText());
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

    public String decode(String password, String salt, String iv, String encryptedData) throws Exception{
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(passwordChars, saltBytes, 10000, 256);
        SecretKey secretKey1 = keyFactory.generateSecret(keySpec);
        SecretKey secretKey = new SecretKeySpec(secretKey1.getEncoded(), "AES");

        byte[] ivBytes = Base64.getDecoder().decode(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        byte[] decodedMessage = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedMessage);

        return new String(decryptedBytes);
    }
    String generatePasswordSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return new BigInteger(1,salt).toString(16);
    }

    void updatePassword(JsonData jsonData, String password) {
        String newSalt = generatePasswordSalt();
        String saltedPassword = password + newSalt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedPassword = digest.digest(saltedPassword.getBytes());
            String passwordHash = new BigInteger(1,encodedPassword).toString(16);
            Path path = Paths.get("data/data.json");
            String jsonContent = "{\n" +
                    "  \"salt\": \"" + newSalt +"\",\n" +
                    "  \"hashedPassword\": \"" + passwordHash +"\",\n" +
                    "  \"iv\": \"" +jsonData.iv + "\",\n" +
                    "  \"encryptedData\": \""+ jsonData.encryptedData +"\"\n" +
                    "}";
            Files.writeString(path, jsonContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
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