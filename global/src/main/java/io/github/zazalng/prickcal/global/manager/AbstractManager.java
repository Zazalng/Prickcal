package io.github.zazalng.prickcal.global.manager;

import io.github.zazalng.prickcal.global.contract.operator.Action;
import io.github.zazalng.prickcal.global.entities.Log;

public abstract class AbstractManager implements Manager {
    protected final RepositoryProvider repos;
    protected final ManagerFactory factory;

    public AbstractManager(ManagerFactory factory) {
        this.factory = factory;
        this.repos = factory.getRepos();
    }

    public abstract String getTableName();

    public abstract <T extends Manager> T initialize();

    public abstract void reload();

    public abstract void shutdown();

    protected AccountManager accountManager() {
        return factory.getManager(ManagersEnum.ACCOUNT);
    }

    protected ApostleManager apostleManager() {
        return factory.getManager(ManagersEnum.APOSTLE);
    }

    protected SessionManager sessionManager() {
        return factory.getManager(ManagersEnum.SESSION);
    }

    /**
     * Create and persist an audit log entry.
     */
    public Log logRecord(String actorUid, Action action, String description) {
        Log log = new Log();
        log.setUid(actorUid);
        log.setTableName(getTableName());
        log.setAction(action.name());
        log.setToString(description);
        factory.getRepos().logs().save(log);
        return log;
    }

    /**
     * Convenience: log a CREATE action.
     */
    public Log logCreated(String actorUid, String description) {
        return logRecord(actorUid, Action.CREATE, description);
    }

    /**
     * Convenience: log an UPDATE action.
     */
    public Log logUpdated(String actorUid, String description) {
        return logRecord(actorUid, Action.UPDATE, description);
    }

    /**
     * Convenience: log a DELETE action.
     */
    public Log logDeleted(String actorUid, String description) {
        return logRecord(actorUid, Action.DELETE, description);
    }
}
