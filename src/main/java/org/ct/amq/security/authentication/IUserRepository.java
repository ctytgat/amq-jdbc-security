package org.ct.amq.security.authentication;

import org.ct.amq.security.User;

import java.util.List;

public interface IUserRepository {
    void initialize();

    List<User> getUsers();

    User getUser(String username);
}
