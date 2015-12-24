package org.ct.amq.jdbc.security.authentication;

import org.apache.activemq.jaas.GroupPrincipal;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String username;
    private String password;
    private boolean enabled;
    private Set<String> roles = new HashSet<>();

    private User() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<Principal> getRolesAsPrincipals() {
        Set<Principal> principals = new HashSet<>();
        for (String role : roles) {
            principals.add(new GroupPrincipal(role));
        }
        return principals;
    }

    public static class Builder {
        private User user = new User();

        public Builder() {
        }

        public Builder withEnabled(boolean enabled) {
            this.user.enabled = enabled;
            return this;
        }

        public Builder withUsername(String username) {
            this.user.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.user.password = password;
            return this;
        }

        public Builder withRole(String role) {
            this.user.roles.add(role);
            return this;
        }

        public User build() {
            user.roles = Collections.unmodifiableSet(user.roles);
            return user;
        }
    }
}
