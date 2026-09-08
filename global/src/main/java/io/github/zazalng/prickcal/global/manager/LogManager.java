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
 * Centralized audit-log factory.
 * Every state-changing operation goes through here so logging is consistent
 * and never accidentally omitted.
 */
public class LogManager {

    private final RepositoryProvider repos;

    public LogManager(RepositoryProvider repos) {
        this.repos = repos;
    }

    /** Create and persist an audit log entry. */
    public Log record(String uid, String table, Action action, String description) {
        Log log = new Log();
        log.setUid(uid);
        log.setTableName(table);
        log.setAction(action.name());
        log.setToString(description);
        repos.logs().save(log);
        return log;
    }

    /** Convenience: log a CREATE action. */
    public Log created(String uid, String table, String description) {
        return record(uid, table, Action.CREATE, description);
    }

    /** Convenience: log an UPDATE action. */
    public Log updated(String uid, String table, String description) {
        return record(uid, table, Action.UPDATE, description);
    }

    /** Convenience: log a DELETE action. */
    public Log deleted(String uid, String table, String description) {
        return record(uid, table, Action.DELETE, description);
    }
}
