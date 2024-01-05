package com.alexcostea.passwordmanager.Controller;

import com.alexcostea.passwordmanager.Domain.Login;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;

public class MainPageController {

    private final ObservableList<Login> logins;

    private Scene addScene;

    private final FXMLLoader viewLoader;

    private final String css;

    private final String password;

    private ViewLoginController ctrl;

    private Scene viewScene;

    @FXML
    protected ListView<Login> loginsView;

    @FXML
    protected TextField generatedPassword;

    public MainPageController(FXMLLoader addLoader, FXMLLoader viewLoader, String css, Stage primaryStage, String password) {
        primaryStage.setOnCloseRequest(event -> saveData());
        this.logins = FXCollections.observableArrayList();
        this.viewLoader = viewLoader;
        this.css = css;
        this.password = password;
        try {
            addLoader.setControllerFactory(param -> new AddLoginController(this.logins));
            this.viewLoader.setControllerFactory(param -> new ViewLoginController(this.logins, null, this.css));
            this.addScene = new Scene(addLoader.load(), 400, 300);
            this.addScene.getStylesheets().add(this.css);
            this.viewScene = new Scene(this.viewLoader.load(), 400, 300);
            this.viewScene.getStylesheets().add(this.css);
            this.ctrl = this.viewLoader.getController();
        } catch (Exception e) {
            System.out.println(e.getClass() + "\n" + e.getMessage());
        }
    }

    public MainPageController(FXMLLoader addLoader, FXMLLoader viewLoader, String css, Stage primaryStage, String password, List<Login> logins) {
        primaryStage.setOnCloseRequest(event -> saveData());
        this.logins = FXCollections.observableList(logins);
        this.viewLoader = viewLoader;
        this.css = css;
        this.password = password;
        try {
            addLoader.setControllerFactory(param -> new AddLoginController(this.logins));
            this.viewLoader.setControllerFactory(param -> new ViewLoginController(this.logins, null, this.css));
            this.addScene = new Scene(addLoader.load(), 400, 300);
            this.addScene.getStylesheets().add(this.css);
            this.viewScene = new Scene(this.viewLoader.load(), 400, 300);
            this.viewScene.getStylesheets().add(this.css);
            this.ctrl = this.viewLoader.getController();
        } catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }

    public void initialize() {
        this.loginsView.setItems(this.logins);
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

    private void saveData() {
        try {
            Path path = Paths.get("data/data.json");
            byte[] bytes = Files.readAllBytes(path);
            String jsonContent = new String(bytes);
            String[] lines = jsonContent.split("\n");
            String data = createJson(this.logins);
            String salt = lines[1].split("\"")[3];
            EncryptionResult encrypted = encrypt(this.password, salt, data);
            String iv = encrypted.iv;
            String encryptedData = encrypted.data;
            String newJson = "{\n" +
                             lines[1] + "\n" +
                             lines[2] + "\n" +
                             "  \"iv\": \"" + iv + "\",\n" +
                             "  \"encryptedData\": \"" + encryptedData + "\"\n" +
                             "}";
            Files.writeString(path, newJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }

    private EncryptionResult encrypt(String password, String salt, String data) throws Exception{
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(passwordChars, saltBytes, 10000, 256);
        SecretKey secretKey1 = keyFactory.generateSecret(keySpec);
        SecretKey secretKey = new SecretKeySpec(secretKey1.getEncoded(), "AES");

        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] ivBytes = new byte[16];
        secureRandom.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        String encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);
        String ivString = Base64.getEncoder().encodeToString(iv.getIV());

        return new EncryptionResult(ivString, encryptedMessage);
    }

    private String createJson(ObservableList<Login> logins) {
        StringBuilder json = new StringBuilder("{\n\"logins\": [\n");
        for(Login login: logins) {
            json.append("   {\n" + "       \"title\": \"")
                    .append(login.getTitle())
                    .append("\",\n")
                    .append("       \"mailOrUsername\": \"")
                    .append(login.getMailOrUsername())
                    .append("\",\n")
                    .append("       \"password\": \"")
                    .append(login.getPassword())
                    .append("\"\n")
                    .append("   },\n");
        }
        json.append(" ]\n}");
        return json.toString();
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

    public void generatePassword() {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder password = new StringBuilder();

        String allCharacters = lowercase + uppercase + digits + symbols;

        for (int i = 0; i < 15; i++) {
            int randomIndex = secureRandom.nextInt(allCharacters.length());
            char randomChar = allCharacters.charAt(randomIndex);
            password.append(randomChar);
        }

        this.generatedPassword.setText(password.toString());
    }


    public static class EncryptionResult {
        public String iv;
        public String data;

        public EncryptionResult(String iv, String data) {
            this.iv = iv;
            this.data = data;
        }
    }
}
