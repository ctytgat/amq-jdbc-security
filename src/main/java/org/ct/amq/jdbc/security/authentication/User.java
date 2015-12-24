package org.ct.amq.jdbc.security.authentication;

import org.apache.activemq.jaas.GroupPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String username;
    private String encryptedPassword;
    private boolean enabled;
    private Set<String> roles = new HashSet<>();
    private final static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private User() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
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

    @Override public String toString() {
        return "User{" +
                "enabled=" + enabled +
                ", username='" + username + '\'' +
                ", password='" + encryptedPassword + '\'' +
                ", roles=" + roles +
                '}';
    }

    public boolean hasPassword(String password) {
        return bCryptPasswordEncoder.matches(password, encryptedPassword);
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
            this.user.encryptedPassword = password;
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
