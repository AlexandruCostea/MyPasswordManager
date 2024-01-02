package com.alexcostea.passwordmanager.Repository;

import com.alexcostea.passwordmanager.Domain.Login;
import java.util.List;

public interface Repository {

    List<Login> getLogins();

    void addLogin(Login login) throws Exception;

    void removeLogin(Login login);

    boolean searchLogin(Login login);
}
