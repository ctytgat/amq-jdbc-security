package org.ct.amq.security;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.ct.amq.security.authentication.JdbcAuthenticationBroker;
import org.ct.amq.security.authentication.UserRepository;

public class JdbcSecurityPlugin implements BrokerPlugin {
    private Datasource datasource = new Datasource();

    public Broker installPlugin(Broker parent) throws Exception {
        return new JdbcAuthenticationBroker(parent, new UserRepository(datasource));
    }
}
