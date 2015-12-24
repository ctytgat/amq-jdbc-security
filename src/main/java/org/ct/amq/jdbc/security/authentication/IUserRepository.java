package org.ct.amq.jdbc.security.authentication;

import java.util.List;

public interface IUserRepository {
    void initialize();

    List<User> getUsers();

    User getUser(String username);
}
