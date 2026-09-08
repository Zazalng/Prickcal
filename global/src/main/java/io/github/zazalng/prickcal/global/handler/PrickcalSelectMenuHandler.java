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
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Thin StringSelectMenu interaction router.
 * Delegates all business logic to the Manager layer.
 */
public class PrickcalSelectMenuHandler {

    private final String stringMenuPrefix;
    private final SessionManager sessionManager;
    private final PanelBuilder panelBuilder;
    private final AccountManager accountManager;
    private final ApostleManager apostleManager;
    private final RepositoryProvider repos;

    public PrickcalSelectMenuHandler(String stringMenuPrefix, SessionManager sessionManager,
                                     PanelBuilder panelBuilder,
                                     AccountManager accountManager, ApostleManager apostleManager,
                                     RepositoryProvider repos) {
        this.stringMenuPrefix = stringMenuPrefix;
        this.sessionManager = sessionManager;
        this.panelBuilder = panelBuilder;
        this.accountManager = accountManager;
        this.apostleManager = apostleManager;
        this.repos = repos;
    }

    public void handle(StringSelectInteractionEvent event) {
        String menuId = event.getComponentId().substring(stringMenuPrefix.length());
        String userId = event.getUser().getId();
        List<String> values = event.getValues();

        switch (menuId) {
            case "select_apostle" -> handleSelectApostle(event, userId, values);
            case "deep_color" -> handleDeepColorFilter(event, userId, values);
            case "deep_position" -> handleDeepPositionFilter(event, userId, values);
            case "deep_race" -> handleDeepRaceFilter(event, userId, values);
        }
    }

    // ==================== SELECT APOSTLE ====================

    private void handleSelectApostle(StringSelectInteractionEvent event, String userId, List<String> values) {
        if (values.isEmpty()) return;
        long apostleId = Long.parseLong(values.get(0));

        var optApostle = apostleManager.findById(apostleId);
        if (optApostle.isEmpty()) return;

        Apostle apostle = optApostle.get();
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

    // ==================== DEEP SEARCH FILTERS ====================

    private void handleDeepColorFilter(StringSelectInteractionEvent event, String userId, List<String> values) {
        List<Apostle> current = sessionManager.getDeepSearchResults(userId);
        if (current == null) current = apostleManager.listAll();
        if (values.isEmpty()) {
            refreshDeepSearch(event, userId, current);
            return;
        }
        int colorNo = Integer.parseInt(values.get(0));
        List<Apostle> filtered = apostleManager.filterByColor(current, colorNo);
        sessionManager.setDeepSearchResults(userId, filtered);
        sessionManager.setDeepSearchPage(userId, 0);
        refreshDeepSearch(event, userId, filtered);
    }

    private void handleDeepPositionFilter(StringSelectInteractionEvent event, String userId, List<String> values) {
        List<Apostle> current = sessionManager.getDeepSearchResults(userId);
        if (current == null) current = apostleManager.listAll();
        if (values.isEmpty()) {
            refreshDeepSearch(event, userId, current);
            return;
        }
        int raceNo = Integer.parseInt(values.get(0));
        List<Apostle> filtered = apostleManager.filterByRace(current, raceNo);
        sessionManager.setDeepSearchResults(userId, filtered);
        sessionManager.setDeepSearchPage(userId, 0);
        refreshDeepSearch(event, userId, filtered);
    }

    private void handleDeepRaceFilter(StringSelectInteractionEvent event, String userId, List<String> values) {
        // "Race" in the entity maps to the same field as position filtering
        List<Apostle> current = sessionManager.getDeepSearchResults(userId);
        if (current == null) current = apostleManager.listAll();
        if (values.isEmpty()) {
            refreshDeepSearch(event, userId, current);
            return;
        }
        int raceNo = Integer.parseInt(values.get(0));
        List<Apostle> filtered = apostleManager.filterByRace(current, raceNo);
        sessionManager.setDeepSearchResults(userId, filtered);
        sessionManager.setDeepSearchPage(userId, 0);
        refreshDeepSearch(event, userId, filtered);
    }

    private void refreshDeepSearch(StringSelectInteractionEvent event, String userId, List<Apostle> results) {
        int page = sessionManager.getDeepSearchPage(userId);
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDeepSearchPanel(userId, results, page, 10))
                        .build()
        ).queue();
    }
}
