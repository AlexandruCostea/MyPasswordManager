package com.alexcostea.passwordmanager.Service;

import com.alexcostea.passwordmanager.Domain.Login;
import com.alexcostea.passwordmanager.Exceptions.RepositoryException;

import javax.crypto.SecretKey;
import java.util.List;

public interface Service {

    List<Login> getData();

    boolean contains(Login login);

    void add(Login login) throws RepositoryException;

    void remove(Login login);

    void addFirst(Login login) throws RepositoryException;

    void saveData();

    void downloadPDF();

}
