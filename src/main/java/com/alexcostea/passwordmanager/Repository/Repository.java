package com.alexcostea.passwordmanager.Repository;

import com.alexcostea.passwordmanager.Domain.Login;
import com.alexcostea.passwordmanager.Exceptions.RepositoryException;

import java.util.List;

public interface Repository {

    List<Login> getData();

    void add(Login data) throws RepositoryException;

    boolean contains(Login data);

    void remove(Login data);

    void addFirst(Login login) throws RepositoryException;
}
