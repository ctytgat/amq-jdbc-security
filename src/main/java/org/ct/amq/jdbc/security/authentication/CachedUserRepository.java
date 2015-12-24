package org.ct.amq.jdbc.security.authentication;

import org.ct.amq.jdbc.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class CachedUserRepository implements IUserRepository {
    private static final Logger logger = LoggerFactory.getLogger(CachedUserRepository.class);

    private IUserRepository delegate;
    private long refreshRateInMillis;

    private volatile Map<String, User> userCache = new HashMap<>();

    public CachedUserRepository(IUserRepository delegate, long refreshRateInMillis) {
        this.delegate = delegate;
        this.refreshRateInMillis = refreshRateInMillis;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userCache.values());
    }

    @Override
    public User getUser(String username) {
        return userCache.get(username);
    }

    @Override
    public void initialize() {
        UsersRefreshJob usersRefreshJob = new UsersRefreshJob();
        usersRefreshJob.run();
        getScheduledThreadPoolExecutor().scheduleWithFixedDelay(usersRefreshJob, refreshRateInMillis, refreshRateInMillis, TimeUnit.MILLISECONDS);
    }

    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "ActiveMQ JDBC Authentication Scheduled Task");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    private class UsersRefreshJob implements Runnable {
        @Override
        public void run() {
            logger.info("Refreshing user list");
            Map<String, User> newUserMap = new HashMap<>();
            try {
                List<User> newUserList = delegate.getUsers();
                for (User user : newUserList) {
                    newUserMap.put(user.getUsername(), user);
                }
            } catch (Exception e) {
                logger.error("Error refreshing user list", e);
            }
            userCache = newUserMap;
        }
    }
}
