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
        String userId = event.getUser().getId();

        switch (menuId) {
            case "select_apostle" -> handleSelectApostle(event, userId);
        }
    }

    // ==================== SELECT APOSTLE ====================

    private void handleSelectApostle(StringSelectInteractionEvent event, String userId) {
        if (event.getValues().isEmpty()) return;
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) return;

        Apostle apostle = apostleManager.findById(Long.parseLong(event.getValues().getFirst()));

        sessionManager.setCurrentApostle(account.get().getId(), apostle);
        ApostleTrack track = apostleManager.findTrack(account.get(), apostle);

        sessionManager.setCrayonToggleState(account.get().getId(), track.getCrayons());

        event.getHook().editOriginalComponents(
                panelBuilder.buildApostleComponent(account.get(), apostle)
        ).queue();

        if (sessionManager.getControlMessages(account.get().getId()) != null) {
            event.getHook().editMessageEmbedsById(sessionManager.getControlMessages(account.get().getId()).getId(),
                    panelBuilder.buildMainMenuEmbed(event.getUser(), account.get())
            ).queue(m -> sessionManager.setControlMessages(account.get().getId(), m));
        }
    }
}
