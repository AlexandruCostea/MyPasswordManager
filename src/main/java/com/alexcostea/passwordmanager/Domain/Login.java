package com.alexcostea.passwordmanager.Domain;

public class Login {
    private String title;
    private String mailOrUsername;
    private String password;

    public Login() {
    }

    public Login(String title, String mailOrUsername, String password) {
        this.title = title;
        this.mailOrUsername = mailOrUsername;
        this.password = password;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMailOrUsername() {
        return this.mailOrUsername;
    }

    public String getPassword() {
        return this.password;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMailOrUsername(String mailOrUsername) {
        this.mailOrUsername = mailOrUsername;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Login login) {
            return this.title.equals(login.getTitle())
                    && this.mailOrUsername.equals(login.getMailOrUsername())
                    && this.password.equals(login.getPassword());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
