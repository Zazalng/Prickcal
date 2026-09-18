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

import io.github.zazalng.prickcal.global.dto.ApostleSearch;
import io.github.zazalng.prickcal.global.entities.Account;
import io.github.zazalng.prickcal.global.entities.Apostle;
import io.github.zazalng.prickcal.global.entities.ApostleTrack;
import net.dv8tion.jda.api.entities.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user session state for the Prickcal plugin.
 * Encapsulates all per-user ephemeral state to avoid scattering maps across the main class.
 */
public class SessionManager extends AbstractManager {
    /**
     * Cache account of user of plugin give advantage efficiency optimize.
     */
    private final Map<String, Account> accountCache = new ConcurrentHashMap<>();
    /**
     * Control panel messages per user (userId -> embedMessage).
     */
    private final Map<Long, Message> controlMessages = new ConcurrentHashMap<>();
    /**
     * Currently viewed apostle per user (userId -> Apostle).
     */
    private final Map<Long, Apostle> currentApostle = new ConcurrentHashMap<>();
    /**
     * Cache state of {@link ApostleTrack}
     */
    private final Map<Long, ApostleTrack> trackedApostleState = new ConcurrentHashMap<>();
    /**
     * Apostle search configuration per user.
     * {inputText ,startIndex, unused_yet, personality no, position no, race no, cache result}
     */
    private final Map<Long, ApostleSearch> apostleSearch = new ConcurrentHashMap<>();

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

    public SessionManager setControlMessages(Long userId, Message messages) {
        controlMessages.put(userId, messages);
        return this;
    }

    public Message getControlMessages(Long userId) {
        return controlMessages.get(userId);
    }

    public Message removeControlMessages(Long userId) {
        return controlMessages.remove(userId);
    }

    // ==================== CURRENT APOSTLE ====================

    public SessionManager setCurrentApostle(Long userId, Apostle apostle) {
        currentApostle.put(userId, apostle);
        return this;
    }

    public Apostle getCurrentApostle(Long userId) {
        return currentApostle.get(userId);
    }

    public Apostle removeCurrentApostle(Long userId) {
        return currentApostle.remove(userId);
    }

    // ==================== CRAYON TOGGLE STATE ====================

    public SessionManager setApostleTrackState(Long userId, ApostleTrack state) {
        trackedApostleState.put(userId, state);
        return this;
    }

    public ApostleTrack getApostleTrackState(Long userId) {
        return trackedApostleState.get(userId);
    }

    public ApostleTrack removeApostleTrackState(Long userId) {
        return trackedApostleState.remove(userId);
    }

    // ==================== Switching Search ====================

    public SessionManager setApostleSearch(Long userId, ApostleSearch config) {
        apostleSearch.put(userId, config);
        return this;
    }

    public ApostleSearch getApostleSearch(Long userId) {
        return apostleSearch.computeIfAbsent(userId, _ -> new ApostleSearch(""));
    }

    public ApostleSearch removeApostleSearch(Long userId) {
        return apostleSearch.remove(userId);
    }

    // ==================== BULK CLEANUP ====================

    public SessionManager clearUserSession(Account account) {
        //Discord Session
        removeControlMessages(account.getId());
        //Prickcal Session
        removeCurrentApostle(account.getId());
        removeApostleTrackState(account.getId());
        removeApostleSearch(account.getId());

        return this;
    }

    public SessionManager clearAllSessions() {
        controlMessages.clear();
        apostleSearch.clear();
        currentApostle.clear();
        trackedApostleState.clear();

        return this;
    }
}
