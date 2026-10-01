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
import net.dv8tion.jda.api.entities.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user session state for the Prickcal plugin.
 * Encapsulates all per-user ephemeral state to avoid scattering maps across the main class.
 */
public class SessionManager extends AbstractManager {
    /**
     * Cache account of {@link Account} that match by {@link User#getId()}
     */
    private final Map<String, Account> accountCache = new ConcurrentHashMap<>();
    /**
     * Control panel messages per user (userId -> embedMessage).
     */
    private final Map<Long, Message> controlMessages = new ConcurrentHashMap<>();
    /**
     * Respond Message playback when doing action (userId -> String)
     */
    private final Map<Long, String> systemMessage = new ConcurrentHashMap<>();
    /**
     * Currently viewed apostle per user (userId -> Apostle).
     */
    private final Map<Long, Apostle> currentApostle = new ConcurrentHashMap<>();
    /**
     * Cache state of {@link ApostleTrack}
     */
    private final Map<Long, ApostleTrack> trackedApostleState = new ConcurrentHashMap<>();
    /**
     * Cache config of {@link ApostleSearch}
     */
    private final Map<Long, ApostleSearch> apostleSearch = new ConcurrentHashMap<>();

    /**
     * Binds the session manager to the factory; no further setup is needed.
     *
     * @param factory the owning factory
     */
    protected SessionManager(ManagerFactory factory) {
        super(factory);
    }

    /**
     * Table name written into the audit log entries of this manager.
     *
     * @return an empty string, as this manager writes no audit log of its own
     */
    @Override
    public String getTableName() {
        return "";
    }

    /**
     * No setup is required; the caches simply start empty.
     *
     * @return this manager
     */
    @Override
    public SessionManager initialize() {
        return this;
    }

    /**
     * Drop every cached session so that the next read starts from a clean state.
     */
    @Override
    public void reload() {
        clearAllSessions();
    }

    /**
     * Drop every cached session when the plugin shuts down.
     */
    @Override
    public void shutdown() {
        clearAllSessions();
    }

    // ==================== ACCOUNT CACHING ====================

    /**
     * Cache the account of a Discord user.
     *
     * @param uid     the Discord user id, used as the cache key
     * @param account the account to cache
     * @return this manager, for chaining
     */
    public SessionManager setAccountCache(String uid, Account account) {
        accountCache.put(uid, account);
        return this;
    }

    /**
     * Read the cached account of a Discord user.
     *
     * @param uid the Discord user id
     * @return the cached account, or null when that user has no cached account
     */
    public Account getAccountCache(String uid) {
        return accountCache.get(uid);
    }

    /**
     * Drop the cached account of a Discord user.
     *
     * @param uid the Discord user id
     * @return the removed account, or null when nothing was cached
     */
    public Account removeAccountCache(String uid) {
        return accountCache.remove(uid);
    }

    // ==================== CONTROL MESSAGES ====================

    /**
     * Cache the control panel message of a user.
     *
     * @param userId   the owning account id, used as the cache key
     * @param messages the control panel message to cache
     * @return this manager, for chaining
     */
    public SessionManager setControlMessages(Long userId, Message messages) {
        controlMessages.put(userId, messages);
        return this;
    }

    /**
     * Read the cached control panel message of a user.
     *
     * @param userId the owning account id
     * @return the cached message, or null when that user has none
     */
    public Message getControlMessages(Long userId) {
        return controlMessages.get(userId);
    }

    /**
     * Drop the cached control panel message of a user.
     *
     * @param userId the owning account id
     * @return the removed message, or null when nothing was cached
     */
    public Message removeControlMessages(Long userId) {
        return controlMessages.remove(userId);
    }

    // ==================== SYSTEM MESSAGES ====================

    /**
     * Cache the response message a user is about to receive.
     *
     * @param userId the owning account id, used as the cache key
     * @param str the response message to cache
     * @return this manager, for chaining
     */
    public SessionManager setSystemMessage(Long userId, String str) {
        systemMessage.put(userId, str);
        return this;
    }

    /**
     * Read the cached response message of a user.
     * An absent entry is stored as an empty string, so the cache always answers.
     *
     * @param userId the owning account id
     * @return the cached response message, empty when none was set
     */
    public String getSystemMessage(Long userId) {
        return systemMessage.computeIfAbsent(userId, _ -> "");
    }

    /**
     * Drop the cached response message of a user.
     *
     * @param userId the owning account id
     * @return the removed message, or null when nothing was cached
     */
    public String removeSystemMessage(Long userId) {
        return systemMessage.remove(userId);
    }

    // ==================== CURRENT APOSTLE ====================

    /**
     * Cache the apostle a user is currently viewing.
     *
     * @param userId the owning account id, used as the cache key
     * @param apostle the apostle being viewed
     * @return this manager, for chaining
     */
    public SessionManager setCurrentApostle(Long userId, Apostle apostle) {
        currentApostle.put(userId, apostle);
        return this;
    }

    /**
     * Read the apostle a user is currently viewing.
     *
     * @param userId the owning account id
     * @return the viewed apostle, or null when none is cached
     */
    public Apostle getCurrentApostle(Long userId) {
        return currentApostle.get(userId);
    }

    /**
     * Drop the apostle a user is currently viewing.
     *
     * @param userId the owning account id
     * @return the removed apostle, or null when nothing was cached
     */
    public Apostle removeCurrentApostle(Long userId) {
        return currentApostle.remove(userId);
    }

    // ==================== CRAYON TOGGLE STATE ====================

    /**
     * Cache the crayon toggle state of a user.
     *
     * @param userId the owning account id, used as the cache key
     * @param state the toggle state to cache
     * @return this manager, for chaining
     */
    public SessionManager setApostleTrackState(Long userId, ApostleTrack state) {
        trackedApostleState.put(userId, state);
        return this;
    }

    /**
     * Read the crayon toggle state of a user.
     *
     * @param userId the owning account id
     * @return the cached toggle state, or null when none is cached
     */
    public ApostleTrack getApostleTrackState(Long userId) {
        return trackedApostleState.get(userId);
    }

    /**
     * Drop the crayon toggle state of a user.
     *
     * @param userId the owning account id
     * @return the removed toggle state, or null when nothing was cached
     */
    public ApostleTrack removeApostleTrackState(Long userId) {
        return trackedApostleState.remove(userId);
    }

    // ==================== Switching Search ====================

    /**
     * Cache the in-progress apostle search of a user.
     *
     * @param userId the owning account id, used as the cache key
     * @param config the search configuration to cache
     * @return this manager, for chaining
     */
    public SessionManager setApostleSearch(Long userId, ApostleSearch config) {
        apostleSearch.put(userId, config);
        return this;
    }

    /**
     * Read the in-progress apostle search of a user.
     * An absent entry is stored as a blank search configuration, so the cache always answers.
     *
     * @param userId the owning account id
     * @return the cached search configuration, a new blank one when none was set
     */
    public ApostleSearch getApostleSearch(Long userId) {
        return apostleSearch.computeIfAbsent(userId, _ -> new ApostleSearch(""));
    }

    /**
     * Drop the in-progress apostle search of a user.
     *
     * @param userId the owning account id
     * @return the removed search configuration, or null when nothing was cached
     */
    public ApostleSearch removeApostleSearch(Long userId) {
        return apostleSearch.remove(userId);
    }

    // ==================== BULK CLEANUP ====================

    /**
     * Drop every cached session entry of a single account, both the Discord user id keyed
     * cache and all account id keyed caches.
     *
     * @param account the account whose sessions are removed
     * @return this manager, for chaining
     */
    public SessionManager clearUserSession(Account account) {
        //Discord Session
        removeAccountCache(account.getUid());
        removeControlMessages(account.getId());
        //Prickcal Session
        removeSystemMessage(account.getId());
        removeCurrentApostle(account.getId());
        removeApostleTrackState(account.getId());
        removeApostleSearch(account.getId());

        return this;
    }

    /**
     * Drop every cached session entry of every user.
     *
     * @return this manager, for chaining
     */
    public SessionManager clearAllSessions() {
        accountCache.clear();
        controlMessages.clear();
        apostleSearch.clear();
        systemMessage.clear();
        currentApostle.clear();
        trackedApostleState.clear();

        return this;
    }
}
