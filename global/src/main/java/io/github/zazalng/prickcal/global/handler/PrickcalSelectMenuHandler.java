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
import io.github.zazalng.prickcal.global.entities.Apostle;
import io.github.zazalng.prickcal.global.manager.*;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.Optional;

/**
 * Thin StringSelectMenu interaction router.
 * Delegates all business logic to the Manager layer.
 */
public class PrickcalSelectMenuHandler {

    private final String stringMenuPrefix;
    private final PanelBuilder panelBuilder;
    private final ManagerFactory factory;
    private final AccountManager accountManager;
    private final ApostleManager apostleManager;
    private final SessionManager sessionManager;

    public PrickcalSelectMenuHandler(String stringMenuPrefix, PanelBuilder panelBuilder, ManagerFactory factory) {
        this.stringMenuPrefix = stringMenuPrefix;
        this.panelBuilder = panelBuilder;
        this.factory = factory;
        accountManager = factory.getManager(ManagersEnum.ACCOUNT);
        apostleManager = factory.getManager(ManagersEnum.APOSTLE);
        sessionManager = factory.getManager(ManagersEnum.SESSION);
    }

    public void handle(StringSelectInteractionEvent event) {
        String menuId = event.getComponentId().substring(stringMenuPrefix.length());

        switch (menuId) {
            case "select_apostle" -> handleSelectApostle(event);
        }
    }

    // ==================== SELECT APOSTLE ====================

    private void handleSelectApostle(StringSelectInteractionEvent event) {
        if (event.getValues().isEmpty()) return;

        String value = event.getValues().getFirst();

        Optional<Account> accountOpt = accountManager.findByUid(event.getUser().getId());

        if (accountOpt.isEmpty()) return;

        Account account = accountOpt.get();

        if (value.startsWith("pagination:")) {
            int direction = Integer.parseInt(value.substring("pagination:".length()));

            ApostleSearch search = sessionManager.getApostleSearch(account.getId());

            if (direction < 0) {
                search.previousPage();
            } else if (direction > 0) {
                search.nextPage();
            }

            sessionManager.setApostleSearch(account.getId(), search);

            event.deferEdit().queue(hook ->
                    hook.editOriginalComponents(
                            panelBuilder.buildApostleListPanel(account)
                    ).useComponentsV2().queue()
            );

            return;
        }

        Apostle apostle = apostleManager.findById(Long.parseLong(value));

        if (apostle == null) return;

        sessionManager.setCurrentApostle(account.getId(), apostle);
        sessionManager.removeApostleTrackState(account.getId());

        event.deferEdit().queue(hook ->
                hook.editOriginalComponents(
                        panelBuilder.buildApostleComponent(account, apostle)
                ).useComponentsV2(true).queue()
        );

        if (sessionManager.getControlMessages(account.getId()) != null) {
            event.getHook().editMessageEmbedsById(
                    sessionManager.getControlMessages(account.getId()).getId(),
                    panelBuilder.buildTrackEmbed(account, apostle)
            ).queue(message ->
                    sessionManager.setControlMessages(account.getId(), message)
            );
        }
    }
}
