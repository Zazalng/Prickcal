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
     */
    public Log logCreated(long actorUid, String description) {
        return logRecord(actorUid, Action.CREATE, description);
    }

    /**
     * Convenience: log an UPDATE action.
     */
    public Log logUpdated(long actorUid, String description) {
        return logRecord(actorUid, Action.UPDATE, description);
    }

    /**
     * Convenience: log a DELETE action.
     */
    public Log logDeleted(long actorUid, String description) {
        return logRecord(actorUid, Action.DELETE, description);
    }
}
