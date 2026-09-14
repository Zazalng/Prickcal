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
    private final Map<String, Message> controlMessages = new ConcurrentHashMap<>();
    /**
     * Currently viewed apostle per user (userId -> Apostle).
     */
    private final Map<Long, Apostle> currentApostle = new ConcurrentHashMap<>();
    /**
     * Current crayon toggle state per user (userId -> boolean[9]).
     */
    private final Map<Long, List<Boolean>> crayonToggleState = new ConcurrentHashMap<>();
    /**
     * Deep search results per user (userId -> List<Apostle>).
     */
    private final Map<Long, List<Apostle>> deepSearchResults = new ConcurrentHashMap<>();
    /**
     * Deep search pagination page per user.
     */
    private final Map<Long, Integer> deepSearchPage = new ConcurrentHashMap<>();

    protected SessionManager(ManagerFactory factory) {
        super(factory);
    }
    /**
     * Apostle panel messages per user (userId -> {embedMessage, interactionMessage}).
     */
    private final Map<String, List<Message>> apostleMessages = new ConcurrentHashMap<>();

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

    }

    @Override
    public void shutdown() {

    }

    /**
     * Public post message IDs per user (userId -> channelId:messageId).
     */
    private final Map<String, String> publicPosts = new ConcurrentHashMap<>();

    // ==================== CONTROL MESSAGES ====================

    public void putControlMessages(String userId, Message messages) {
        controlMessages.put(userId, messages);
    }

    public Message getControlMessages(String userId) {
        return controlMessages.get(userId);
    }

    public Message removeControlMessages(String userId) {
        return controlMessages.remove(userId);
    }

    // ==================== APOSTLE MESSAGES ====================

    public void putApostleMessages(String userId, List<Message> messages) {
        apostleMessages.put(userId, messages);
    }

    public List<Message> getApostleMessages(String userId) {
        return apostleMessages.get(userId);
    }

    public List<Message> removeApostleMessages(String userId) {
        return apostleMessages.remove(userId);
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

    // ==================== DEEP SEARCH ====================

    public void setDeepSearchResults(Long userId, List<Apostle> results) {
        deepSearchResults.put(userId, results);
    }

    public List<Apostle> getDeepSearchResults(Long userId) {
        return deepSearchResults.get(userId);
    }

    public void removeDeepSearchResults(Long userId) {
        deepSearchResults.remove(userId);
    }

    public int getDeepSearchPage(Long userId) {
        return deepSearchPage.getOrDefault(userId, 0);
    }

    public void setDeepSearchPage(Long userId, int page) {
        deepSearchPage.put(userId, Math.max(0, page));
    }

    public void removeDeepSearchPage(Long userId) {
        deepSearchPage.remove(userId);
    }

    // ==================== PUBLIC POSTS ====================

    public void putPublicPost(String userId, String channelMessageId) {
        publicPosts.put(userId, channelMessageId);
    }

    public String getPublicPost(String userId) {
        return publicPosts.get(userId);
    }

    public String removePublicPost(String userId) {
        return publicPosts.remove(userId);
    }

    // ==================== BULK CLEANUP ====================

    public void clearUserSession(Account account) {
        //Discord Session
        removeControlMessages(account.getUid());
        removeApostleMessages(account.getUid());
        removePublicPost(account.getUid());
        //Prickcal Session
        removeCurrentApostle(account.getId());
        removeCrayonToggleState(account.getId());
        removeDeepSearchResults(account.getId());
        removeDeepSearchPage(account.getId());
    }

    public void clearAllSessions() {
        controlMessages.values().forEach(msgs -> {
            for (Message msg : msgs) {
                try {
                    msg.delete().queue(null, _ -> {
                    });
                } catch (Exception ignored) {
                }
            }
        });
        apostleMessages.values().forEach(msgs -> {
            for (Message msg : msgs) {
                try {
                    msg.delete().queue(null, _ -> {
                    });
                } catch (Exception ignored) {
                }
            }
        });
        controlMessages.clear();
        apostleMessages.clear();
        currentApostle.clear();
        crayonToggleState.clear();
        deepSearchResults.clear();
        deepSearchPage.clear();
        publicPosts.clear();
    }
}
