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
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles StringSelectMenu interactions for the Prickcal plugin.
 */
public class PrickcalSelectMenuHandler {

    private final String stringMenuPrefix;
    private final SessionManager sessionManager;
    private final PanelBuilder panelBuilder;
    private final PrickcalButtonHandler.PluginRepoProvider repos;

    public PrickcalSelectMenuHandler(String stringMenuPrefix, SessionManager sessionManager,
                                     PanelBuilder panelBuilder, PrickcalButtonHandler.PluginRepoProvider repos) {
        this.stringMenuPrefix = stringMenuPrefix;
        this.sessionManager = sessionManager;
        this.panelBuilder = panelBuilder;
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

    private void handleSelectApostle(StringSelectInteractionEvent event, String userId, List<String> values) {
        if (values.isEmpty()) return;
        long apostleId = Long.parseLong(values.get(0));

        Apostle apostle = repos.apostles().query()
                .where("id", apostleId)
                .findOne().orElse(null);
        if (apostle == null) return;

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

    private void handleDeepColorFilter(StringSelectInteractionEvent event, String userId, List<String> values) {
        List<Apostle> current = sessionManager.getDeepSearchResults(userId);
        if (current == null) {
            current = repos.apostles().query().list();
        }
        if (values.isEmpty()) {
            refreshDeepSearch(event, userId, current);
            return;
        }
        int colorNo = Integer.parseInt(values.get(0));
        List<Apostle> filtered = current.stream()
                .filter(a -> a.getColor() == colorNo)
                .collect(Collectors.toList());
        sessionManager.setDeepSearchResults(userId, filtered);
        sessionManager.setDeepSearchPage(userId, 0);
        refreshDeepSearch(event, userId, filtered);
    }

    private void handleDeepPositionFilter(StringSelectInteractionEvent event, String userId, List<String> values) {
        List<Apostle> current = sessionManager.getDeepSearchResults(userId);
        if (current == null) {
            current = repos.apostles().query().list();
        }
        if (values.isEmpty()) {
            refreshDeepSearch(event, userId, current);
            return;
        }
        int posNo = Integer.parseInt(values.get(0));
        List<Apostle> filtered = current.stream()
                .filter(a -> a.getRace() == posNo)
                .collect(Collectors.toList());
        sessionManager.setDeepSearchResults(userId, filtered);
        sessionManager.setDeepSearchPage(userId, 0);
        refreshDeepSearch(event, userId, filtered);
    }

    private void handleDeepRaceFilter(StringSelectInteractionEvent event, String userId, List<String> values) {
        List<Apostle> current = sessionManager.getDeepSearchResults(userId);
        if (current == null) {
            current = repos.apostles().query().list();
        }
        if (values.isEmpty()) {
            refreshDeepSearch(event, userId, current);
            return;
        }
        int raceNo = Integer.parseInt(values.get(0));
        List<Apostle> filtered = current.stream()
                .filter(a -> a.getRace() == raceNo)
                .collect(Collectors.toList());
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
