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

/**
 * Creates, owns and hands out the plugin's managers.
 * The constructor starts every manager, so all of them are reachable through
 * {@link #getManager(ManagersEnum)} as soon as the factory exists.
 */
public class ManagerFactory {
    private final PluginContext ctx;
    private final RepositoryProvider repos;
    private final EnumMap<ManagersEnum, Manager> managers = new EnumMap<>(ManagersEnum.class);

    /**
     * Stores the plugin context and repository provider, then starts every manager.
     *
     * @param ctx   the host plugin context
     * @param repos the repository provider shared with every manager
     */
    public ManagerFactory(PluginContext ctx, RepositoryProvider repos) {
        this.ctx = ctx;
        this.repos = repos;
        startAllManagers();
    }

    /**
     * Expose the host plugin context to the managers.
     *
     * @return the context this factory was built with
     */
    public PluginContext getCtx() {
        return ctx;
    }

    /**
     * Expose the shared repository provider to the managers.
     *
     * @return the provider this factory was built with
     */
    public RepositoryProvider getRepos() {
        return repos;
    }

    /**
     * Look up a manager by its enum key and narrow it to the expected class.
     *
     * @param managerName the key the manager is registered under
     * @param <T>         the expected manager type, taken from {@link ManagersEnum#getManagerClass()}
     * @return the registered manager, cast to {@code T}
     * @throws IllegalArgumentException if the key is unregistered, or the registered
     *                                  manager is not an instance of the expected class
     */
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

    /**
     * Register an already constructed manager under a key.
     *
     * @param managerName the key to register under
     * @param manager the manager instance to register
     * @return this factory, so registrations can be chained
     */
    private ManagerFactory loadManager(ManagersEnum managerName, Manager manager) {
        this.managers.put(managerName, manager);
        return this;
    }

    /**
     * Reload one registered manager, if the key is registered at all.
     *
     * @param managerName the key of the manager to reload
     * @return this factory, for chaining
     */
    public ManagerFactory reloadManager(ManagersEnum managerName) {
        Manager manager = this.managers.get(managerName);
        if (manager != null) {
            manager.reload();
        }
        return this;
    }

    /**
     * Shut every registered manager down and drop all registrations.
     *
     * @return this factory, for chaining
     */
    public ManagerFactory shutdownAllManagers() {
        this.managers.values().forEach(Manager::shutdown);
        this.managers.clear();
        return this;
    }

    /**
     * Construct and register every manager declared by {@link ManagersEnum}.
     *
     * @return this factory, for chaining
     */
    public ManagerFactory startAllManagers() {
        loadManager(ManagersEnum.ACCOUNT, new AccountManager(this))
                .loadManager(ManagersEnum.APOSTLE, new ApostleManager(this))
                .loadManager(ManagersEnum.SESSION, new SessionManager(this))
        ;
        return this;
    }
}
