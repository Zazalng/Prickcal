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

    /**
     * Creates the modal router for one plugin schema.
     *
     * @param modalPrefix  the prefix carried by every modal custom ID this handler dispatches
     * @param panelBuilder the builder that renders every panel this handler edits
     * @param factory      the manager factory used to resolve the session, account and apostle managers
     */
    public PrickcalModalHandler(String modalPrefix, PanelBuilder panelBuilder, ManagerFactory factory) {
        this.modalPrefix = modalPrefix;
        this.panelBuilder = panelBuilder;
        this.factory = factory;
        sessionManager = factory.getManager(ManagersEnum.SESSION);
        accountManager = factory.getManager(ManagersEnum.ACCOUNT);
        apostleManager = factory.getManager(ManagersEnum.APOSTLE);
    }

    /**
     * Routes a modal submission to the handler that owns its custom ID.
     * <p>
     * The id is stripped of {@code modalPrefix}: the {@code apostle_} family (the deep search modal opened as
     * {@code apostle_switch_search}) goes to {@link #handleSwitchApostleSearch(ModalInteractionEvent)} and the
     * {@code profile_} family to {@link #handleProfileUpdate(ModalInteractionEvent)}. Any other id is ignored.
     *
     * @param event the modal interaction to dispatch
     */
    public void handle(ModalInteractionEvent event) {
        String modalId = event.getModalId().substring(modalPrefix.length());

        if (modalId.startsWith("apostle_")) {
            handleSwitchApostleSearch(event);
        } else if (modalId.startsWith("profile_")) {
            handleProfileUpdate(event);
        }
    }

    // ==================== SWITCH APOSTLE SEARCH ====================

    /**
     * Serves the apostle search modal by filtering the apostle list and redrawing it.
     * <p>
     * The optional name is trimmed and lower-cased, the three checkbox groups are converted to raw stat
     * numbers, and the resulting {@link ApostleSearch} is filled through {@link ApostleManager#deepFilter(ApostleSearch)}
     * and stored in the {@link SessionManager} so paging and rendering can reuse it. The list panel replaces the
     * modal's message and the control message embed is refreshed for the current apostle when one is registered.
     *
     * @param event the {@code apostle_*} modal interaction
     */
    private void handleSwitchApostleSearch(ModalInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());

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

    /**
     * Serves the {@code profile_update_<section>} modal by writing the submitted text back to the account.
     * <p>
     * {@code ign} and {@code code} are assigned directly, while {@code format} goes through
     * {@link Account#validateFormat(String)} so an invalid format is dropped. A blank submission is ignored
     * entirely, otherwise the account is saved to the repository. The profile panel replaces the modal's
     * message and the control message embed is refreshed to the new profile summary.
     *
     * @param event the {@code profile_update_*} modal interaction
     */
    private void handleProfileUpdate(ModalInteractionEvent event) {
        String section = event.getModalId().substring((modalPrefix + "profile_update_").length());
        Account account = sessionManager.getAccountCache(event.getUser().getId());

        String value = Optional.ofNullable(event.getValue(section))
                .map(m -> m.getAsString().trim())
                .filter(s -> !s.isEmpty())
                .orElse("");

        if(!value.isEmpty()) {
            switch(section) {
                case "ign" -> account.setIgn(value);
                case "code" -> account.setFriendCode(value);
                case "format" -> account.validateFormat(value);
            }

            factory.getRepos().accounts().save(account);
        }

        event.deferEdit().queue(i ->
                i.editOriginalComponents(
                        panelBuilder.buildProfileComponent(account)
                ).useComponentsV2(true).queue()
        );

        event.getHook().editMessageEmbedsById(
                sessionManager.getControlMessages(account.getId()).getId(),
                panelBuilder.buildMainMenuEmbed(event.getUser(), account)
        ).queue(m -> sessionManager.setControlMessages(account.getId(), m));
    }

    // ==================== Helper ====================

    /**
     * Converts a checkbox group's selected values into their raw stat numbers.
     * <p>
     * An absent mapping (unchecked group) yields a fresh empty mutable list, not {@code null}.
     *
     * @param value the modal mapping of a checkbox group
     * @return the selected values parsed as {@code short}s, or an empty list when the group was not submitted
     */
    private List<Short> mapToShort(ModalMapping value) {
        return Optional.ofNullable(value)
                .map(v -> v.getAsStringList().stream()
                        .map(Short::valueOf)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }
}
