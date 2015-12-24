package org.ct.amq.security.authentication;

import org.ct.amq.security.Datasource;
import org.ct.amq.security.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository implements IUserRepository {

    private Datasource datasource;

    public UserRepository(Datasource datasource) {
        this.datasource = datasource;
    }

    @Override public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = datasource.getConnection();
            Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select * from USERS left join AUTHORITIES on USERS.username = AUTHORITIES.username")) {
                Map<String, User.Builder> builders = new HashMap<>();
                while(resultSet.next()) {
                    String username = resultSet.getString("username");
                    User.Builder builder = builders.get(username);
                    if (builder == null) {
                        builder = new User.Builder();
                        builder.withUsername(username);
                        builder.withPassword(resultSet.getString("password"));
                        builder.withEnabled(resultSet.getBoolean("enabled"));
                        builders.put(username, builder);
                    }
                    String authority = resultSet.getString("authority");
                    if (authority != null) {
                        builder.withRole(authority);
                    }
                }

                for (User.Builder builder : builders.values()) {
                    users.add(builder.build());
                }
            }
        } catch (SQLException e) {
            throw new SecurityException("Could not retrieve users", e);
        }

        return users;
    }

    @Override public User getUser(String username) {
        try (Connection connection = datasource.getConnection();
            Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select * from USERS left join AUTHORITIES on USERS.username = AUTHORITIES.username where USERS.username='" + username + "'")) {
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

    @Override public void initialize() {
        // nothing to do
    }
}
