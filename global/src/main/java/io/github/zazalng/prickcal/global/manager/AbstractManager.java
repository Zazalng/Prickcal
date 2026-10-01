/*
 * Prickcal - A Trickcal's procession tracker for Pudel Bot
 * Copyright (C) 2026 Napapon Kamanee
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.zazalng.prickcal.global.manager;

import io.github.zazalng.prickcal.global.contract.operator.Action;
import io.github.zazalng.prickcal.global.entities.Log;

/**
 * Base class for every manager, wiring the shared repository provider and
 * factory into the concrete managers and hosting the audit log helpers.
 */
public abstract class AbstractManager implements Manager {
    protected final RepositoryProvider repos;
    protected final ManagerFactory factory;

    /**
     * Binds the manager to the shared repository provider and factory.
     *
     * @param factory the owning factory, used to reach sibling managers and the repositories
     */
    public AbstractManager(ManagerFactory factory) {
        this.factory = factory;
        this.repos = factory.getRepos();
    }

    /**
     * Name of the table this manager writes into audit log entries.
     *
     * @return the table name passed to {@link #logRecord(long, Action, String)}
     */
    public abstract String getTableName();

    /**
     * Prepare this manager for use right after construction.
     *
     * @param <T> the concrete manager type, allowing chaining without a cast
     * @return this manager, typed as {@code T}
     */
    public abstract <T extends Manager> T initialize();

    /**
     * Refresh any cached state from the backing repositories.
     * Implementations without a cache may leave this empty.
     */
    public abstract void reload();

    /**
     * Release any cached state or resource held by this manager.
     * Implementations without state may leave this empty.
     */
    public abstract void shutdown();

    /**
     * Look up the shared account manager through the factory.
     *
     * @return the registered {@link AccountManager}
     */
    protected AccountManager accountManager() {
        return factory.getManager(ManagersEnum.ACCOUNT);
    }

    /**
     * Look up the shared apostle manager through the factory.
     *
     * @return the registered {@link ApostleManager}
     */
    protected ApostleManager apostleManager() {
        return factory.getManager(ManagersEnum.APOSTLE);
    }

    /**
     * Look up the shared session manager through the factory.
     *
     * @return the registered {@link SessionManager}
     */
    protected SessionManager sessionManager() {
        return factory.getManager(ManagersEnum.SESSION);
    }

    /**
     * Create and persist an audit log entry attributed to this manager's table.
     *
     * @param actorUid the account id of the actor, or {@code 0} when there is no acting user
     * @param action the audited action, stored by {@link Action#name()}
     * @param description the human readable message stored on the log row
     * @return the persisted log entity
     */
    public Log logRecord(long actorUid, Action action, String description) {
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
     *
     * @param actorUid the account id of the actor, or {@code 0} when there is no acting user
     * @param description the human readable message stored on the log row
     * @return the persisted log entity
     */
    public Log logCreated(long actorUid, String description) {
        return logRecord(actorUid, Action.CREATE, description);
    }

    /**
     * Convenience: log an UPDATE action.
     *
     * @param actorUid the account id of the actor, or {@code 0} when there is no acting user
     * @param description the human readable message stored on the log row
     * @return the persisted log entity
     */
    public Log logUpdated(long actorUid, String description) {
        return logRecord(actorUid, Action.UPDATE, description);
    }

    /**
     * Convenience: log a DELETE action.
     *
     * @param actorUid the account id of the actor, or {@code 0} when there is no acting user
     * @param description the human readable message stored on the log row
     * @return the persisted log entity
     */
    public Log logDeleted(long actorUid, String description) {
        return logRecord(actorUid, Action.DELETE, description);
    }
}
