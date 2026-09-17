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
import io.github.zazalng.prickcal.global.manager.*;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Thin modal-interaction router.
 * Delegates all business logic to the Manager layer.
 */
public class PrickcalModalHandler {
    private final String modalPrefix;
    private final PanelBuilder panelBuilder;
    private final ManagerFactory factory;
    private final SessionManager sessionManager;
    private final AccountManager accountManager;
    private final ApostleManager apostleManager;

    public PrickcalModalHandler(String modalPrefix, PanelBuilder panelBuilder, ManagerFactory factory) {
        this.modalPrefix = modalPrefix;
        this.panelBuilder = panelBuilder;
        this.factory = factory;
        sessionManager = factory.getManager(ManagersEnum.SESSION);
        accountManager = factory.getManager(ManagersEnum.ACCOUNT);
        apostleManager = factory.getManager(ManagersEnum.APOSTLE);
    }

    public void handle(ModalInteractionEvent event) {
        String modalId = event.getModalId().substring(modalPrefix.length());

        switch (modalId) {
            case "switch_apostle_search" -> handleSwitchApostleSearch(event);
        }
    }

    // ==================== SWITCH APOSTLE SEARCH ====================

    private void handleSwitchApostleSearch(ModalInteractionEvent event) {
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) return;

        String searchName = Optional.ofNullable(event.getValue("search_name"))
                .map(v -> v.getAsString().trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .orElse("");

        String raceFilters = Optional.ofNullable(event.getValue("filter_race"))
                .map(v -> String.join(",", v.getAsStringList()))
                .orElse("");

        String colorFilters = Optional.ofNullable(event.getValue("filter_color"))
                .map(v -> String.join(",", v.getAsStringList()))
                .orElse("");

        String positionFilters = Optional.ofNullable(event.getValue("filter_position"))
                .map(v -> String.join(",", v.getAsStringList()))
                .orElse("");

        sessionManager.setApostleSearch(account.get().getId(), new ArrayList<>(Arrays.asList(searchName, "1", "23", raceFilters, colorFilters, positionFilters, "")));

        event.getHook().editOriginalComponents(
                panelBuilder.buildApostleListPanel(account.get())
        ).queue();
    }
}
