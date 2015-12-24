package org.ct.amq.jdbc.security.authentication;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.ct.amq.jdbc.security.User;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Set;

public class JdbcAuthenticationBroker extends AbstractAuthenticationBroker {
    private IUserRepository userRepository;

    public JdbcAuthenticationBroker(Broker next, IUserRepository userRepository) {
        super(next);
        this.userRepository = userRepository;
    }

    @Override
    public void start() throws Exception {
        userRepository.initialize();

        super.start();
    }

    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        SecurityContext securityContext = context.getSecurityContext();
        if (securityContext == null) {
            securityContext = authenticate(info.getUserName(), info.getPassword(), null);
            context.setSecurityContext(securityContext);
            securityContexts.add(securityContext);
        }

        try {
            super.addConnection(context, info);
        } catch (Exception e) {
            securityContexts.remove(securityContext);
            context.setSecurityContext(null);
            throw e;
        }
    }

    public SecurityContext authenticate(String username, String password, X509Certificate[] x509Certificates) throws SecurityException {
        final User user = userRepository.getUser(username);
        if (user == null || !user.getPassword().equals(password) || !user.isEnabled()) {
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
