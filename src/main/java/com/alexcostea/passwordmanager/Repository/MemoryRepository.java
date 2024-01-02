package com.alexcostea.passwordmanager.Repository;

import com.alexcostea.passwordmanager.Domain.Login;

import java.util.List;
import java.util.LinkedList;

public class MemoryRepository implements Repository {

    private final LinkedList<Login> logins;

    public MemoryRepository() {
        this.logins = new LinkedList<>();
    }

    @Override
    public List<Login> getLogins() {
        return this.logins;
    }

    @Override
    public void addLogin(Login login) throws Exception{
        if(this.searchLogin(login))
            throw new Exception("Login already exists!");
        this.logins.add(login);
    }

    @Override
    public void removeLogin(Login login) {
        this.logins.remove(login);
    }

    @Override
    public boolean searchLogin(Login login) {
        return this.logins.contains(login);
    }
}
