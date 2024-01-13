package com.alexcostea.passwordmanager.Repository;

import com.alexcostea.passwordmanager.Domain.Login;
import com.alexcostea.passwordmanager.Exceptions.RepositoryException;

import java.util.ArrayList;
import java.util.List;

public class MemoryRepository implements Repository {

    private final List<Login> data;

    public MemoryRepository() {
        this.data = new ArrayList<>();
    }

    public MemoryRepository(List<Login> data) {
        this.data = data;
    }

    @Override
    public List<Login> getData() {
        return this.data;
    }

    @Override
    public void add(Login data) throws RepositoryException {
        if(this.data.contains(data))
            throw new RepositoryException("Data already in the repository");
        this.data.add(data);
    }

    @Override
    public void addFirst(Login data) throws RepositoryException {
        if(this.data.contains(data))
            throw new RepositoryException("Data already in the repository");
        this.data.addFirst(data);
    }

    @Override
    public boolean contains(Login data) {
        return this.data.contains(data);
    }

    @Override
    public void remove(Login data) {
        this.data.remove(data);
    }
}
