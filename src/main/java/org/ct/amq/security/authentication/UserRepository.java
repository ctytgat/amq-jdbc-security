package org.ct.amq.security.authentication;

import org.ct.amq.security.Datasource;
import org.ct.amq.security.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserRepository {

    private Datasource datasource;

    public UserRepository(Datasource datasource) {
        this.datasource = datasource;
    }

    public User findUser(String username) {
        try (Connection connection = datasource.getConnection();
            Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select * from USERS left join AUTHORITIES on USERS.username = AUTHORITIES.username where username='" + username + "'")) {
                if (resultSet.next()) {
                    User.Builder builder = new User.Builder();
                    builder.withUsername(username);
                    builder.withPassword(resultSet.getString("password"));
                    builder.withEnabled(resultSet.getBoolean("enabled"));
                    do {
                        builder.withRole(resultSet.getString("authority"));
                    } while(resultSet.next());

                    return builder.build();
                }

                return null;
            }
        } catch (SQLException e) {
            throw new SecurityException("Could not retrieve user " + username, e);
        }
    }
}
