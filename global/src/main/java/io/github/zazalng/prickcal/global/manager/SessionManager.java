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

import io.github.zazalng.prickcal.global.entities.Account;
import io.github.zazalng.prickcal.global.entities.Apostle;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user session state for the Prickcal plugin.
 * Encapsulates all per-user ephemeral state to avoid scattering maps across the main class.
 */
public class SessionManager extends AbstractManager {
    /**
     * Control panel messages per user (userId -> embedMessage).
     */
    private final Map<Long, Message> controlMessages = new ConcurrentHashMap<>();
    /**
     * Currently viewed apostle per user (userId -> Apostle).
     */
    private final Map<Long, Apostle> currentApostle = new ConcurrentHashMap<>();
    /**
     * Current crayon toggle state per user (userId -> boolean[9]).
     */
    private final Map<Long, List<Boolean>> crayonToggleState = new ConcurrentHashMap<>();
    /**
     * Apostle search configuration per user.
     * {inputText ,startIndex, startIndex+23, personality no, position no, race no, cache result}
     */
    private final Map<Long, List<String>> apostleSearch = new ConcurrentHashMap<>();

    protected SessionManager(ManagerFactory factory) {
        super(factory);
    }

    @Override
    public String getTableName() {
        return "";
    }

    @Override
    public SessionManager initialize() {
        return this;
    }

    @Override
    public void reload() {
        clearAllSessions();
    }

    @Override
    public void shutdown() {
        clearAllSessions();
    }

    // ==================== CONTROL MESSAGES ====================

    public void setControlMessages(Long userId, Message messages) {
        controlMessages.put(userId, messages);
    }

    public Message getControlMessages(Long userId) {
        return controlMessages.get(userId);
    }

    public Message removeControlMessages(Long userId) {
        return controlMessages.remove(userId);
    }

    // ==================== CURRENT APOSTLE ====================

    public void setCurrentApostle(Long userId, Apostle apostle) {
        currentApostle.put(userId, apostle);
    }

    public Apostle getCurrentApostle(Long userId) {
        return currentApostle.get(userId);
    }

    public void removeCurrentApostle(Long userId) {
        currentApostle.remove(userId);
    }

    // ==================== CRAYON TOGGLE STATE ====================

    public void setCrayonToggleState(Long userId, List<Boolean> state) {
        crayonToggleState.put(userId, state);
    }

    public List<Boolean> getCrayonToggleState(Long userId) {
        return crayonToggleState.get(userId);
    }

    public void removeCrayonToggleState(Long userId) {
        crayonToggleState.remove(userId);
    }

    // ==================== Switching Search ====================
    /**
     * {Apostle.name, StartIndexPagination, EndIndexPagination, RaceFilter, ColorFilter, PositionFilter, Apostle.id}
     */
    public void setApostleSearch(Long userId, List<String> config) {
        apostleSearch.put(userId, config);
    }

    /**
     * {Apostle.name, StartIndexPagination, EndIndexPagination, RaceFilter, ColorFilter, PositionFilter, Apostle.id}
     */
    public List<String> getApostleSearch(Long userId) {
        return apostleSearch.computeIfAbsent(userId, _ -> new ArrayList<>(Arrays.asList("", "1", "23", "", "", "", "")));
    }

    public void removeApostleSearch(Long userId) {
        apostleSearch.remove(userId);
    }

    // ==================== BULK CLEANUP ====================

    public SessionManager clearUserSession(Account account) {
        //Discord Session
        removeControlMessages(account.getId());
        //Prickcal Session
        removeCurrentApostle(account.getId());
        removeCrayonToggleState(account.getId());
        removeApostleSearch(account.getId());

        return this;
    }

    public SessionManager clearAllSessions() {
        controlMessages.clear();
        apostleSearch.clear();
        currentApostle.clear();
        crayonToggleState.clear();

        return this;
    }
}
