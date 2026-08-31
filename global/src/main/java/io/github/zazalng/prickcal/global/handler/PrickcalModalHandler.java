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
import io.github.zazalng.prickcal.global.entities.ApostleTrack;
import io.github.zazalng.prickcal.global.entities.CrayonLineUp;
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
 * Handles modal submissions for the Prickcal plugin.
 */
public class PrickcalModalHandler {

    private final String modalPrefix;
    private final PanelBuilder panelBuilder;
    private final SessionManager sessionManager;
    private final PrickcalButtonHandler.PluginRepoProvider repos;

    public PrickcalModalHandler(String modalPrefix, PanelBuilder panelBuilder,
                                SessionManager sessionManager, PrickcalButtonHandler.PluginRepoProvider repos) {
        this.modalPrefix = modalPrefix;
        this.panelBuilder = panelBuilder;
        this.sessionManager = sessionManager;
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

        List<Apostle> allApostles = repos.apostles().query().list();
        List<Apostle> results;

        if (searchName != null) {
            results = searchByName(allApostles, searchName);
            if (results.size() == 1) {
                selectAndShowApostle(event, userId, results.get(0));
                return;
            }
        } else {
            results = new ArrayList<>(allApostles);
        }

        if (!colorFilters.isEmpty() && results.size() != 1) {
            List<Apostle> filtered = results.stream()
                    .filter(a -> colorFilters.contains(a.getColor()))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                results = filtered;
                if (results.size() == 1) {
                    selectAndShowApostle(event, userId, results.get(0));
                    return;
                }
            }
        }

        if (!positionFilters.isEmpty() && results.size() != 1) {
            List<Apostle> filtered = results.stream()
                    .filter(a -> positionFilters.contains(a.getRace()))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                results = filtered;
                if (results.size() == 1) {
                    selectAndShowApostle(event, userId, results.get(0));
                    return;
                }
            }
        }

        if (!colorFilters.isEmpty() && !positionFilters.isEmpty() && results.size() != 1) {
            List<Apostle> filtered = results.stream()
                    .filter(a -> colorFilters.contains(a.getColor()))
                    .filter(a -> positionFilters.contains(a.getRace()))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                results = filtered;
                if (results.size() == 1) {
                    selectAndShowApostle(event, userId, results.get(0));
                    return;
                }
            }
        }

        if (results.size() != 1) {
            sessionManager.setDeepSearchResults(userId, results);
            sessionManager.setDeepSearchPage(userId, 0);

            event.editMessage(
                    new MessageEditBuilder()
                            .useComponentsV2(true)
                            .setComponents(panelBuilder.buildDeepSearchPanel(
                                    userId, results, 0, 10))
                            .build()
            ).queue();
        }
    }

    private void handleDeepNameSearch(ModalInteractionEvent event, String userId) {
        String name = Optional.ofNullable(event.getValue("deep_name"))
                .map(v -> v.getAsString().trim().toLowerCase())
                .orElse("");

        List<Apostle> currentResults = sessionManager.getDeepSearchResults(userId);
        if (currentResults == null) {
            currentResults = repos.apostles().query().list();
        }

        List<Apostle> filtered = currentResults.stream()
                .filter(a -> a.getName() != null && a.getName().toLowerCase().contains(name))
                .collect(Collectors.toList());

        if (filtered.size() == 1) {
            selectAndShowApostle(event, userId, filtered.get(0));
            return;
        }

        sessionManager.setDeepSearchResults(userId, filtered);
        sessionManager.setDeepSearchPage(userId, 0);

        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDeepSearchPanel(
                                userId, filtered, 0, 10))
                        .build()
        ).queue();
    }

    private List<Apostle> searchByName(List<Apostle> apostles, String name) {
        return apostles.stream()
                .filter(a -> a.getName() != null
                        && a.getName().toLowerCase().contains(name))
                .collect(Collectors.toList());
    }

    private void selectAndShowApostle(ModalInteractionEvent event, String userId, Apostle apostle) {
        sessionManager.setCurrentApostle(userId, apostle);

        Account account = repos.accounts().query()
                .where("uid", userId)
                .findOne().orElse(null);

        ApostleTrack track = repos.apostleTrackers().query()
                .where("uid", userId)
                .where("apostleId", apostle.getId())
                .findOne().orElse(null);

        CrayonLineUp lineUp = apostle.getCrayon() != null
                ? repos.crayonLineups().query()
                .where("id", apostle.getCrayon())
                .findOne().orElse(null)
                : null;

        if (track != null) {
            List<Boolean> crayons = track.getCrayons();
            boolean[] state = new boolean[crayons.size()];
            for (int i = 0; i < crayons.size(); i++) {
                state[i] = crayons.get(i);
            }
            sessionManager.setCrayonToggleState(userId, state);
        }

        User discordUser = repos.getDiscordUser(userId);
        Account displayAccount = account != null ? account : new Account();
        if (displayAccount.getUid() == null) {
            displayAccount.setUid(userId);
        }

        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildApostleComponent(
                                userId, apostle, track, lineUp,
                                sessionManager.getCrayonToggleState(userId)))
                        .build()
        ).queue();

        User displayUser = discordUser != null ? discordUser : event.getUser();
        event.getHook().sendMessageEmbeds(
                panelBuilder.buildApostleEmbed(displayUser, displayAccount, apostle, track, lineUp)
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
