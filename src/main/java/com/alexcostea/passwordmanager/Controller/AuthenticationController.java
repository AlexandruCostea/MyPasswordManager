package com.alexcostea.passwordmanager.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AuthenticationController {
    @FXML
    private Label welcomeText;

    @FXML
    protected TextField textField;

    @FXML
    protected void tryAuthentication() {
        String input = this.textField.getText();
        String password = "myPassword";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            byte[] encodedInput = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String stringHash = new BigInteger(1, encodedHash).toString(16);

            System.out.println(stringHash);

            if(Arrays.toString(encodedHash).equals(Arrays.toString(encodedInput)))
                System.out.println("Password is correct");
            else
                System.out.println("Wrong password");
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
    }
}