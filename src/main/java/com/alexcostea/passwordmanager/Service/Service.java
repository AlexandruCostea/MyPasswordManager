package com.alexcostea.passwordmanager.Service;

import com.alexcostea.passwordmanager.Domain.Login;
import com.alexcostea.passwordmanager.Exceptions.RepositoryException;
import com.alexcostea.passwordmanager.Repository.MemoryRepository;
import com.alexcostea.passwordmanager.Repository.Repository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

public class Service {

    private Repository repo;

    private final SecretKey encryptKey;

    private final String newSalt;

    private final String newHash;

    private byte[] newIv;

    public Service(SecretKey decryptKey, SecretKey encryptKey, String newSalt, String newHash) {
        this.encryptKey = encryptKey;
        this.newSalt = newSalt;
        this.newHash = newHash;
        try {
            Path path = Paths.get("data/data.json");
            String content = new String(Files.readAllBytes(path));
            byte[] iv;
            if(content.isEmpty()) {
                SecureRandom secureRandom = SecureRandom.getInstanceStrong();
                iv = new byte[16];
                secureRandom.nextBytes(iv);
                this.newIv = iv;
                this.repo = new MemoryRepository();
            }
            else {
                String ivString = content.split(":")[3].split(",")[0].split("\"")[1];
                String dataString = content.split(":")[4].split("}")[0].split("\"")[1];
                iv = Base64.getDecoder().decode(ivString);
                SecureRandom secureRandom = SecureRandom.getInstanceStrong();
                this.newIv = new byte[16];
                secureRandom.nextBytes(this.newIv);
                dataString = decode(decryptKey, iv, dataString);

                ObjectMapper mapper = new ObjectMapper();
                LoginWrapper data = mapper.readValue
                        (dataString, LoginWrapper.class);
                List<Login> logins = data.getLogins();
                logins.removeLast();
                this.repo = new MemoryRepository(logins);
            }
            saveData();
        } catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }

    public List<Login> getData() {
        return this.repo.getData();
    }

    public boolean contains(Login data) {
        return this.repo.contains(data);
    }

    public void add(Login login) throws RepositoryException {
        if(login.getTitle().isEmpty() || login.getPassword().isEmpty())
            throw new RepositoryException("Empty mandatory fields");
        this.repo.add(login);
    }

    public void remove(Login login) {
        this.repo.remove(login);
    }

    public void addFirst(Login login) throws RepositoryException {
        this.repo.addFirst(login);
    }

    private String createDataJson() {
        StringBuilder json = new StringBuilder("{\n\"logins\": [\n");
        for(Login login: this.repo.getData()) {
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
        json.append("   {\n" + "       \"title\": \"\",\n")
                .append("       \"mailOrUsername\": \"\",\n")
                .append("       \"password\": \"\"\n")
                .append("   }\n");
        json.append(" ]\n}");
        return json.toString();
    }

    public void saveData() {
        try {
            Path path = Paths.get("data/data.json");
            String encodedData = encode(this.encryptKey, createDataJson());
            String newIvString = Base64.getEncoder().encodeToString(this.newIv);
            String newJson = "{\n" +
                    "   \"salt\": \"" + this.newSalt + "\",\n" +
                    "   \"hashedPassword\": \"" + this.newHash + "\",\n" +
                    "   \"iv\": \"" + newIvString + "\",\n" +
                    "   \"encryptedData\": \"" + encodedData + "\"\n" +
                    "}";
            Files.writeString(path, newJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            System.out.println(e.getClass() + e.getMessage());
        }
    }

    public String decode(SecretKey key, byte[] iv, String data) throws Exception{
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] decodedMessage = Base64.getDecoder().decode(data);
        byte[] decryptedBytes = cipher.doFinal(decodedMessage);

        return new String(decryptedBytes);
    }

    private String encode(SecretKey secretKey, String data) throws Exception{

        IvParameterSpec iv = new IvParameterSpec(this.newIv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static class LoginWrapper {
        @JsonProperty("logins")
        private List<Login> logins;

        public List<Login> getLogins() {
            return logins;
        }

        public void setLogins(List<Login> logins) {
            this.logins = logins;
        }
    }
}
