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
import io.github.zazalng.prickcal.global.dto.ApostleSearch;
import io.github.zazalng.prickcal.global.entities.Account;
import io.github.zazalng.prickcal.global.manager.*;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

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

        if (modalId.startsWith("apostle_")) {
            handleSwitchApostleSearch(event);
        } else if (modalId.startsWith("profile_")) {
            handleProfileUpdate(event);
        }
    }

    // ==================== SWITCH APOSTLE SEARCH ====================

    private void handleSwitchApostleSearch(ModalInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        String searchName = Optional.ofNullable(event.getValue("search_name"))
                .map(v -> v.getAsString().trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .orElse("");

        ApostleSearch config = new ApostleSearch(
                searchName,
                mapToShort(event.getValue("filter_race")),
                mapToShort(event.getValue("filter_color")),
                mapToShort(event.getValue("filter_position"))
        );

        config.getSfResult().addAll(apostleManager.deepFilter(config));

        sessionManager.setApostleSearch(account.getId(), config);

        event.deferEdit().queue(i ->
                i.editOriginalComponents(
                        panelBuilder.buildApostleListPanel(account)
                ).useComponentsV2(true).queue(_ -> {
                    if (sessionManager.getControlMessages(account.getId()) != null) {
                        event.getHook().editMessageEmbedsById(
                                sessionManager
                                        .getControlMessages(account.getId())
                                        .getId(),
                                panelBuilder.buildTrackEmbed(
                                        account,
                                        sessionManager.getCurrentApostle(account.getId())
                                )
                        ).queue(message ->
                                sessionManager.setControlMessages(
                                        account.getId(),
                                        message
                                )
                        );
                    }
                })
        );
    }

    // ==================== SWITCH APOSTLE SEARCH ====================

    private void handleProfileUpdate(ModalInteractionEvent event) {

    }

    // ==================== Helper ====================

    private List<Short> mapToShort(ModalMapping value) {
        return Optional.ofNullable(value)
                .map(v -> v.getAsStringList().stream()
                        .map(Short::valueOf)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }
}
