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
package io.github.zazalng.prickcal.global.session;

import io.github.zazalng.prickcal.global.entities.Apostle;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user session state for the Prickcal plugin.
 * Encapsulates all per-user ephemeral state to avoid scattering maps across the main class.
 */
public class SessionManager {
    /**
     * Control panel messages per user (userId -> {embedMessage, interactionMessage}).
     */
    private final Map<String, List<Message>> controlMessages = new ConcurrentHashMap<>();
    /**
     * Apostle panel messages per user (userId -> {embedMessage, interactionMessage}).
     */
    private final Map<String, List<Message>> apostleMessages = new ConcurrentHashMap<>();

    /**
     * Currently viewed apostle per user (userId -> Apostle).
     */
    private final Map<String, Apostle> currentApostle = new ConcurrentHashMap<>();
    /**
     * Current crayon toggle state per user (userId -> boolean[9]).
     */
    private final Map<String, boolean[]> crayonToggleState = new ConcurrentHashMap<>();

    /**
     * Deep search results per user (userId -> List<Apostle>).
     */
    private final Map<String, List<Apostle>> deepSearchResults = new ConcurrentHashMap<>();
    /**
     * Deep search pagination page per user.
     */
    private final Map<String, Integer> deepSearchPage = new ConcurrentHashMap<>();

    /**
     * Public post message IDs per user (userId -> channelId:messageId).
     */
    private final Map<String, String> publicPosts = new ConcurrentHashMap<>();

    // ==================== CONTROL MESSAGES ====================

    public void putControlMessages(String userId, List<Message> messages) {
        controlMessages.put(userId, messages);
    }

    public List<Message> getControlMessages(String userId) {
        return controlMessages.get(userId);
    }

    public List<Message> removeControlMessages(String userId) {
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

    public void setCurrentApostle(String userId, Apostle apostle) {
        currentApostle.put(userId, apostle);
    }

    public Apostle getCurrentApostle(String userId) {
        return currentApostle.get(userId);
    }

    public void removeCurrentApostle(String userId) {
        currentApostle.remove(userId);
    }

    // ==================== CRAYON TOGGLE STATE ====================

    public void setCrayonToggleState(String userId, boolean[] state) {
        crayonToggleState.put(userId, state);
    }

    public boolean[] getCrayonToggleState(String userId) {
        return crayonToggleState.get(userId);
    }

    public void removeCrayonToggleState(String userId) {
        crayonToggleState.remove(userId);
    }

    // ==================== DEEP SEARCH ====================

    public void setDeepSearchResults(String userId, List<Apostle> results) {
        deepSearchResults.put(userId, results);
    }

    public List<Apostle> getDeepSearchResults(String userId) {
        return deepSearchResults.get(userId);
    }

    public void removeDeepSearchResults(String userId) {
        deepSearchResults.remove(userId);
    }

    public int getDeepSearchPage(String userId) {
        return deepSearchPage.getOrDefault(userId, 0);
    }

    public void setDeepSearchPage(String userId, int page) {
        deepSearchPage.put(userId, Math.max(0, page));
    }

    public void removeDeepSearchPage(String userId) {
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

    public void clearUserSession(String userId) {
        removeControlMessages(userId);
        removeApostleMessages(userId);
        removeCurrentApostle(userId);
        removeCrayonToggleState(userId);
        removeDeepSearchResults(userId);
        removeDeepSearchPage(userId);
        removePublicPost(userId);
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
