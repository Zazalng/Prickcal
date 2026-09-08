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
package io.github.zazalng.prickcal.global.handler;

import io.github.zazalng.prickcal.global.builder.PanelBuilder;
import io.github.zazalng.prickcal.global.entities.Account;
import io.github.zazalng.prickcal.global.entities.Apostle;
import io.github.zazalng.prickcal.global.entities.CrayonLineUp;
import io.github.zazalng.prickcal.global.manager.AccountManager;
import io.github.zazalng.prickcal.global.manager.ApostleManager;
import io.github.zazalng.prickcal.global.manager.RepositoryProvider;
import io.github.zazalng.prickcal.global.session.SessionManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Thin modal-interaction router.
 * Delegates all business logic to the Manager layer.
 */
public class PrickcalModalHandler {

    private final String modalPrefix;
    private final PanelBuilder panelBuilder;
    private final SessionManager sessionManager;
    private final AccountManager accountManager;
    private final ApostleManager apostleManager;
    private final RepositoryProvider repos;

    public PrickcalModalHandler(String modalPrefix, PanelBuilder panelBuilder,
                                SessionManager sessionManager,
                                AccountManager accountManager, ApostleManager apostleManager,
                                RepositoryProvider repos) {
        this.modalPrefix = modalPrefix;
        this.panelBuilder = panelBuilder;
        this.sessionManager = sessionManager;
        this.accountManager = accountManager;
        this.apostleManager = apostleManager;
        this.repos = repos;
    }

    public void handle(ModalInteractionEvent event) {
        String modalId = event.getModalId().substring(modalPrefix.length());
        String userId = event.getUser().getId();

        switch (modalId) {
            case "switch_apostle_search" -> handleSwitchApostleSearch(event, userId);
            case "deep_name_search" -> handleDeepNameSearch(event, userId);
        }
    }

    // ==================== SWITCH APOSTLE SEARCH ====================

    private void handleSwitchApostleSearch(ModalInteractionEvent event, String userId) {
        String searchName = Optional.ofNullable(event.getValue("search_name"))
                .map(v -> v.getAsString().trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .orElse(null);

        List<Integer> colorFilters = Optional.ofNullable(event.getValue("filter_color"))
                .map(v -> v.getAsStringList().stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()))
                .orElse(List.of());

        List<Integer> positionFilters = Optional.ofNullable(event.getValue("filter_position"))
                .map(v -> v.getAsStringList().stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()))
                .orElse(List.of());

        List<Apostle> allApostles = apostleManager.listAll();
        List<Apostle> results = apostleManager.deepFilter(allApostles, searchName,
                colorFilters, positionFilters);

        if (results.size() == 1) {
            selectAndShowApostle(event, userId, results.get(0));
            return;
        }

        // Store paginated results
        sessionManager.setDeepSearchResults(userId, results);
        sessionManager.setDeepSearchPage(userId, 0);

        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDeepSearchPanel(userId, results, 0, 10))
                        .build()
        ).queue();
    }

    // ==================== DEEP NAME SEARCH ====================

    private void handleDeepNameSearch(ModalInteractionEvent event, String userId) {
        String name = Optional.ofNullable(event.getValue("deep_name"))
                .map(v -> v.getAsString().trim().toLowerCase())
                .orElse("");

        List<Apostle> currentResults = sessionManager.getDeepSearchResults(userId);
        if (currentResults == null) {
            currentResults = apostleManager.listAll();
        }

        List<Apostle> filtered = apostleManager.searchByName(currentResults, name);

        if (filtered.size() == 1) {
            selectAndShowApostle(event, userId, filtered.get(0));
            return;
        }

        sessionManager.setDeepSearchResults(userId, filtered);
        sessionManager.setDeepSearchPage(userId, 0);

        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDeepSearchPanel(userId, filtered, 0, 10))
                        .build()
        ).queue();
    }

    // ==================== SHARED APOSTLE DISPLAY ====================

    /**
     * Shared display logic used by all three handlers.
     * Selects the apostle, fetches account/track/lineup, updates the
     * interaction message, and manages the embed message lifecycle.
     */
    private void selectAndShowApostle(ModalInteractionEvent event, String userId, Apostle apostle) {
        sessionManager.setCurrentApostle(userId, apostle);

        var optAccount = accountManager.findByUid(userId);
        var optTrack = apostleManager.findTrack(userId, apostle.getId());
        CrayonLineUp lineUp = apostleManager.findLineUp(apostle).orElse(null);

        // Restore toggle state from DB
        optTrack.ifPresent(track -> {
            boolean[] state = ApostleManager.listToState(track.getCrayons());
            sessionManager.setCrayonToggleState(userId, state);
        });

        User discordUser = repos.getDiscordUser(userId);
        Account displayAccount = optAccount.orElseGet(() -> {
            Account a = new Account();
            a.setUid(userId);
            return a;
        });

        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildApostleComponent(
                                userId, apostle, optTrack.orElse(null), lineUp,
                                sessionManager.getCrayonToggleState(userId)))
                        .build()
        ).queue();

        User displayUser = discordUser != null ? discordUser : event.getUser();
        event.getHook().sendMessageEmbeds(
                panelBuilder.buildApostleEmbed(displayUser, displayAccount, apostle,
                        optTrack.orElse(null), lineUp)
        ).queue(msg -> {
            List<Message> msgs = sessionManager.getApostleMessages(userId);
            if (msgs != null) {
                for (Message m : msgs) {
                    try {
                        m.delete().queue(null, _ -> {
                        });
                    } catch (Exception ignored) {
                    }
                }
            }
            sessionManager.putApostleMessages(userId, new ArrayList<>(List.of(msg)));
        });
    }
}
