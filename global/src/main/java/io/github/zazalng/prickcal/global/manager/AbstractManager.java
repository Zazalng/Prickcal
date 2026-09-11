package io.github.zazalng.prickcal.global.manager;

import io.github.zazalng.prickcal.global.contract.operator.Action;
import io.github.zazalng.prickcal.global.entities.Log;

public abstract class AbstractManager implements Manager {
    protected final ManagerFactory factory;

    public AbstractManager(ManagerFactory factory) {
        this.factory = factory;
    }

    public abstract String getTableName();

    public abstract <T extends Manager> T initialize();

    public abstract void reload();

    public abstract void shutdown();

    /**
     * Create and persist an audit log entry.
     */
    public Log logRecord(String uid, Action action, String description) {
        Log log = new Log();
        log.setUid(uid);
        log.setTableName(getTableName());
        log.setAction(action.name());
        log.setToString(description);
        factory.getRepos().logs().save(log);
        return log;
    }

    /**
     * Convenience: log a CREATE action.
     */
    public Log logCreated(String uid, String description) {
        return logRecord(uid, Action.CREATE, description);
    }

    /**
     * Convenience: log an UPDATE action.
     */
    public Log logUpdated(String uid, String description) {
        return logRecord(uid, Action.UPDATE, description);
    }

    /**
     * Convenience: log a DELETE action.
     */
    public Log logDeleted(String uid, String description) {
        return logRecord(uid, Action.DELETE, description);
    }
}
