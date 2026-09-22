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

import group.worldstandard.pudel.api.PluginContext;

import java.util.EnumMap;

public class ManagerFactory {
    private final PluginContext ctx;
    private final RepositoryProvider repos;
    private final EnumMap<ManagersEnum, Manager> managers = new EnumMap<>(ManagersEnum.class);

    public ManagerFactory(PluginContext ctx, RepositoryProvider repos) {
        this.ctx = ctx;
        this.repos = repos;
        startAllManagers();
    }

    public PluginContext getCtx() {
        return ctx;
    }

    public RepositoryProvider getRepos() {
        return repos;
    }

    public <T extends Manager> T getManager(ManagersEnum managerName) {
        Manager manager = this.managers.get(managerName);
        Class<T> expectedClass = managerName.getManagerClass();

        if (expectedClass.isInstance(manager)) {
            return expectedClass.cast(manager);
        }

        throw new IllegalArgumentException(
                "Manager with name " + managerName + " is not of type " + expectedClass.getName()
        );
    }

    private ManagerFactory loadManager(ManagersEnum managerName, Manager manager) {
        this.managers.put(managerName, manager);
        return this;
    }

    public ManagerFactory reloadManager(ManagersEnum managerName) {
        Manager manager = this.managers.get(managerName);
        if (manager != null) {
            manager.reload();
        }
        return this;
    }

    public ManagerFactory shutdownAllManagers() {
        this.managers.values().forEach(Manager::shutdown);
        this.managers.clear();
        return this;
    }

    public ManagerFactory startAllManagers() {
        loadManager(ManagersEnum.ACCOUNT, new AccountManager(this))
                .loadManager(ManagersEnum.APOSTLE, new ApostleManager(this))
                .loadManager(ManagersEnum.SESSION, new SessionManager(this))
        ;
        return this;
    }
}
