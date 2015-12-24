package org.ct.amq.security;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.ct.amq.security.authentication.CachedUserRepository;
import org.ct.amq.security.authentication.IUserRepository;
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
    private Long cacheRefreshInterval;

    public JdbcSecurityPlugin(Datasource datasource, Long cacheRefreshInterval) {
        this.datasource = datasource;
        this.cacheRefreshInterval = cacheRefreshInterval;
    }

    public Broker installPlugin(Broker parent) throws Exception {
        IUserRepository userRepository = new UserRepository(datasource);
        if (cacheRefreshInterval != null && cacheRefreshInterval > 0) {
            userRepository = new CachedUserRepository(userRepository, cacheRefreshInterval);
        }

        return new JdbcAuthenticationBroker(parent, userRepository);
    }
}
