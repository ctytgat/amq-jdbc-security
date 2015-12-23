package org.ct.amq.security.authentication;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.ct.amq.security.User;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Set;

public class JdbcAuthenticationBroker extends AbstractAuthenticationBroker {
    private UserRepository userRepository;

    public JdbcAuthenticationBroker(Broker next, UserRepository userRepository) {
        super(next);
        this.userRepository = userRepository;
    }

    public SecurityContext authenticate(String username, String password, X509Certificate[] x509Certificates) throws SecurityException {
        final User user = userRepository.findUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new SecurityException("User name [" + username + "] or password is invalid.");
        }

        final Set<Principal> rolesAsPrincipals = user.getRolesAsPrincipals();
        return new SecurityContext(username) {
            @Override
            public Set<Principal> getPrincipals() {
                return rolesAsPrincipals;
            }
        };
    }
}
