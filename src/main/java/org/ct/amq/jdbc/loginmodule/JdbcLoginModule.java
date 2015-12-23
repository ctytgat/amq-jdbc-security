//package org.ct.amq.jdbc.loginmodule;
//
//import org.apache.activemq.jaas.GroupPrincipal;
//import org.apache.activemq.jaas.UserPrincipal;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.security.auth.Subject;
//import javax.security.auth.callback.Callback;
//import javax.security.auth.callback.CallbackHandler;
//import javax.security.auth.callback.NameCallback;
//import javax.security.auth.callback.PasswordCallback;
//import javax.security.auth.callback.UnsupportedCallbackException;
//import javax.security.auth.login.FailedLoginException;
//import javax.security.auth.login.LoginException;
//import javax.security.auth.spi.LoginModule;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//public class JdbcLoginModule implements LoginModule {
//
//    private static final Logger log = LoggerFactory.getLogger(JdbcLoginModule.class);
//    private Subject subject;
//    private CallbackHandler callbackHandler;
//    private String user;
//    private Set principals;
//    private String dbUrl;
//    private String dbDriver;
//    private String dbUser;
//    private String dbPassword;
//    private Connection dbConnection;
//    private boolean loginSucceeded;
//
//    public JdbcLoginModule() {
//        principals = new HashSet();
//    }
//
//    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
//        this.subject = subject;
//        this.callbackHandler = callbackHandler;
//        this.loginSucceeded = false;
//
//        dbUrl = (String) options.get("url");
//        dbDriver = (String) options.get("driver");
//        dbUser = (String) options.get("user");
//        dbPassword = (String) options.get("password");
//
//        try {
//            Class.forName(dbDriver);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public boolean login() throws LoginException {
//        Callback callbacks[] = new Callback[2];
//        callbacks[0] = new NameCallback("Username: ");
//        callbacks[1] = new PasswordCallback("Password: ", false);
//        try {
//            callbackHandler.handle(callbacks);
//        } catch (IOException ioe) {
//            throw new LoginException(ioe.getMessage());
//        } catch (UnsupportedCallbackException uce) {
//            throw new LoginException(uce.getMessage() + " not available to obtain information from user");
//        }
//
//        user = ((NameCallback) callbacks[0]).getName();
//        char[] tmpPassword = ((PasswordCallback) callbacks[1]).getPassword();
//        if (tmpPassword == null) {
//            tmpPassword = new char[0];
//        }
//        if (user == null) {
//            throw new FailedLoginException("user name is null");
//        }
//        String password;
//        try {
//            password = findPassword(user);
//        } catch (SQLException e) {
//            log.error("Cannot read password for user " + user, e);
//            throw new FailedLoginException("Cannot retrieve user");
//        }
//
//        if (password == null) {
//            throw new FailedLoginException("User does exist");
//        }
//
//        if (!password.equals(new String(tmpPassword))) {
//            throw new FailedLoginException("Password does not match");
//        }
//
//        loginSucceeded = true;
//
//        log.debug("login " + user);
//
//        return loginSucceeded;
//    }
//
//    private String findPassword(String user) throws SQLException {
//        String sql = "select password from users where username='" + user + "'";
//
//        Statement statement = dbConnection.createStatement();
//        ResultSet rs = statement.executeQuery(sql);
//        if (rs.next()) {
//            return rs.getString("password");
//        }
//
//        return null;
//    }
//
//    public boolean commit() throws LoginException {
//        boolean result = loginSucceeded;
//        if (result) {
//            principals.add(new UserPrincipal(user));
//
//            Set<String> matchedGroups = groups.get(user);
//            if (matchedGroups != null) {
//                for (String entry : matchedGroups) {
//                    principals.add(new GroupPrincipal(entry));
//                }
//            }
//
//            subject.getPrincipals().addAll(principals);
//        }
//
//        // will whack loginSucceeded
//        clear();
//
//        log.debug("commit, result: " + result);
//        return result;
//    }
//
//    public boolean abort() throws LoginException {
//        clear();
//        log.debug("abort");
//        return true;
//    }
//
//    public boolean logout() throws LoginException {
//        subject.getPrincipals().removeAll(principals);
//        principals.clear();
//        clear();
//        log.debug("logout");
//        return true;
//    }
//
//    private void clear() {
//        user = null;
//        loginSucceeded = false;
//    }
//
//}