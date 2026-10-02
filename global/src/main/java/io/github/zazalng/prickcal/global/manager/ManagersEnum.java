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
 * Registry of every manager the plugin can hold, keyed by a stable name.
 * Each constant carries the manager class that {@link ManagerFactory#getManager(ManagersEnum)}
 * narrows its lookup result to.
 */
public enum ManagersEnum {
    ACCOUNT(AccountManager.class),
    APOSTLE(ApostleManager.class),
    SESSION(SessionManager.class);

    private final Class<? extends Manager> managerClass;

    /**
     * Binds the constant to the manager class it represents.
     *
     * @param managerClass the manager class registered for this key
     * @param <T>          the manager type, bounded by {@link Manager}
     */
    <T extends Manager> ManagersEnum(Class<T> managerClass) {
        this.managerClass = managerClass;
    }

    /**
     * The manager class this key resolves to.
     *
     * @param <T> the manager type, bounded by {@link Manager}
     * @return the manager class registered for this constant
     */
    @SuppressWarnings("unchecked")
    public <T extends Manager> Class<T> getManagerClass() {
        return (Class<T>) this.managerClass;
    }
}
