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

/**
 * Common contract for every manager in the plugin.
 * A manager owns one slice of state or persistence concern and is reachable
 * through {@link ManagerFactory} by its {@link ManagersEnum} key.
 */
public interface Manager {
    /**
     * Prepare this manager for use right after construction.
     *
     * @param <T> the concrete manager type, allowing chaining without a cast
     * @return this manager, typed as {@code T}
     */
    <T extends Manager> T initialize();

    /**
     * Refresh any state this manager holds from its backing source.
     * Implementations without a cache may leave this empty.
     */
    void reload();

    /**
     * Release any state or resource this manager holds.
     * Called by {@link ManagerFactory#shutdownAllManagers()}.
     */
    void shutdown();
}
