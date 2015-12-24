package org.ct.amq.security;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.ct.amq.security.authentication.JdbcAuthenticationBroker;
import org.ct.amq.security.authentication.UserRepository;

/**
 * JDBC security plugin
 *
 * @org.apache.xbean.XBean element="jdbcSecurityPlugin"
 *                         description="Provides jdbc security plugin
 *                         configured with a datasource"
 *
 */
public class JdbcSecurityPlugin implements BrokerPlugin {
    private Datasource datasource;

    public JdbcSecurityPlugin(Datasource datasource) {
        this.datasource = datasource;
    }

    public Broker installPlugin(Broker parent) throws Exception {
        return new JdbcAuthenticationBroker(parent, new UserRepository(datasource));
    }
}
