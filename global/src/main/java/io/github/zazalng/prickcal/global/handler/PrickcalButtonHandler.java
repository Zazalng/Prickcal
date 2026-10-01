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
import io.github.zazalng.prickcal.global.contract.operator.Operator;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleColor;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostlePosition;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleRace;
import io.github.zazalng.prickcal.global.entities.Account;
import io.github.zazalng.prickcal.global.entities.Apostle;
import io.github.zazalng.prickcal.global.entities.ApostleTrack;
import io.github.zazalng.prickcal.global.entities.Log;
import io.github.zazalng.prickcal.global.manager.*;
import io.github.zazalng.prickcal.global.util.LabelByEnum;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Thin button-interaction router.
 * Delegates all business logic to the Manager layer so that
 * multiple contributors can reason about data flow without wading
 * through JDA event plumbing.
 */
public class PrickcalButtonHandler {

    private final String btnPrefix;
    private final String modalPrefix;
    private final PanelBuilder panelBuilder;
    private final ManagerFactory factory;
    private final AccountManager accountManager;
    private final ApostleManager apostleManager;
    private final SessionManager sessionManager;

    public PrickcalButtonHandler(String btnPrefix, String modalPrefix,
                                 PanelBuilder panelBuilder, ManagerFactory factory) {
        this.btnPrefix = btnPrefix;
        this.modalPrefix = modalPrefix;
        this.panelBuilder = panelBuilder;
        this.factory = factory;
        sessionManager = factory.getManager(ManagersEnum.SESSION);
        accountManager = factory.getManager(ManagersEnum.ACCOUNT);
        apostleManager = factory.getManager(ManagersEnum.APOSTLE);
    }

    public void handle(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        if (guild == null || member == null) return;

        String buttonId = event.getComponentId().substring(btnPrefix.length());

        if (buttonId.startsWith("consent_")) {
            handleConsent(event);
        } else if (buttonId.startsWith("apostle")) {
            if (buttonId.equals("apostle_switching")) {
                handleApostleSwitching(event);
            } else if (buttonId.startsWith("apostle_increase_")) {
                handleApostleTrackStarIncreasing(event, Boolean.parseBoolean(buttonId.substring("apostle_increase_".length())));
            } else {
                handleApostle(event);
            }
        } else if (buttonId.startsWith("crayon")) {
            if (buttonId.startsWith("crayon_toggle_")) {
                handleCrayonToggle(event, buttonId);
            } else if (buttonId.equals("crayon_confirm")) {
                handleCrayonConfirm(event);
            } else if (buttonId.equals("crayon_reset")) {
                handleCrayonReset(event);
            } else {
                handleAdministrator(event);
            }
        } else if (buttonId.startsWith("profile")) {
            if(buttonId.endsWith("_leak")) {
                handleProfileLeak(event);
            } else if (buttonId.contains("_")) {
                // {ign, code, format}
                handleProfileUpdate(event, buttonId.substring("profile_".length()));
            } else {
                handleProfile(event);
            }
        } else if (buttonId.equals("import_export")) {
            handleImportExport(event);
        } else if (buttonId.equals("database")) {
            handleDatabase(event);
        } else if (buttonId.equals("administrator")) {
            handleAdministrator(event);
        } else if (buttonId.equals("logs")) {
            handleLogs(event);
        } else if (buttonId.startsWith("post_")) {
            if (buttonId.endsWith("apostle")) handlePostApostle(event);
            else if (buttonId.endsWith("tracker")) handlePostTracker(event);
            else if (buttonId.endsWith("profile")) handlePostProfile(event);
        } else if (buttonId.equals("delete_data")) {
            handleDeleteData(event);
        } else if (buttonId.startsWith("delete_")) {
            handleDeleteConfirm(event, buttonId);
        } else if (buttonId.equals("deep_search_modal")) {
            handleDeepSearchModal(event);
        } else if (buttonId.equals("back_main")) {
            handleBackMain(event);
        }
    }

    // ==================== CONSENT ====================

    private void handleConsent(ButtonInteractionEvent event) {
        String action = event.getComponentId().substring(btnPrefix.length());
        switch (action) {
            case "consent_agree" -> {
                Account account = accountManager.createAccount(event.getJDA().getSelfUser().getId(), event.getUser().getId());
                sessionManager.setAccountCache(event.getUser().getId(), account);
                event.getHook().deleteOriginal().queue();

                event.getInteraction().getHook().sendMessageEmbeds(
                        panelBuilder.buildMainMenuEmbed(event.getUser(), account)
                ).setEphemeral(true).queue(m -> sessionManager.setControlMessages(account.getId(), m));

                event.deferReply(true).queue(i ->
                        i.sendMessageComponents(panelBuilder.buildMainMenuComponent(account))
                                .useComponentsV2(true)
                                .setEphemeral(true)
                                .queue()
                );
            }
            case "consent_disagree" -> event.deferEdit().queue(i -> {
                i.deleteOriginal().queue();
                i.sendMessage("❌ Consent denied. Your data will not be tracked. Use `/prickcal` if you change your mind.")
                        .setEphemeral(true)
                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            });
        }
    }

    // ==================== APOSTLE ====================

    private void handleApostle(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        // Pick the first apostle as default
        List<Apostle> all = apostleManager.listAll();
        if (all.isEmpty()) {
            event.reply("❌ No apostles found in database.").setEphemeral(true).queue();
            return;
        }
        Apostle apostle = all.get(new Random().nextInt(all.size()));
        sessionManager.setCurrentApostle(account.getId(), apostle);
        showApostlePanel(event, account, apostle);
    }

    private void showApostlePanel(ButtonInteractionEvent event, Account account, Apostle apostle) {
        event.deferEdit().queue(i ->
                i.editOriginalComponents(panelBuilder.buildApostleComponent(account, apostle))
                        .useComponentsV2(true)
                        .queue()
        );

        event.getHook().editMessageEmbedsById(sessionManager.getControlMessages(account.getId()).getId(),
                panelBuilder.buildTrackEmbed(account, apostle)
        ).queue(
                m -> sessionManager.setControlMessages(account.getId(), m)
        );
    }

    private void handleApostleSwitching(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        event.deferEdit().queue(i ->
                i.editOriginalComponents(
                        panelBuilder.buildApostleListPanel(account))
                        .useComponentsV2()
                        .queue()
        );

        if (sessionManager.getControlMessages(account.getId()) != null) {
            event.getHook().editMessageEmbedsById(sessionManager.getControlMessages(account.getId()).getId(),
                    panelBuilder.buildTrackEmbed(account, sessionManager.getCurrentApostle(account.getId()))
            ).queue(m -> sessionManager.setControlMessages(account.getId(), m));
        }
    }

    private void handleApostleTrackStarIncreasing(ButtonInteractionEvent event, boolean b) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        ApostleTrack track = sessionManager.getApostleTrackState(account.getId());
        Apostle apostle = sessionManager.getCurrentApostle(account.getId());
        track.updateCurrentStar(apostle, b);
        showApostlePanel(event, account, apostle);
    }

    // ==================== CRAYON ====================

    private void handleCrayonToggle(ButtonInteractionEvent event, String buttonId) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        Apostle apostle = sessionManager.getCurrentApostle(account.getId());
        if (apostle == null) return;

        int index = Integer.parseInt(buttonId.substring("crayon_toggle_".length()));
        ApostleTrack track = sessionManager.getApostleTrackState(account.getId());
        List<Boolean> state = track.getCrayons();
        if (state == null) return;

        state.set(index, !state.get(index));
        if (state.contains(true) && track.getCurrentStar() < apostle.getInit()) track.setCurrentStar(apostle.getInit());

        sessionManager.setApostleTrackState(account.getId(), track.updateCrayon(state));

        showApostlePanel(event, account, apostle);
    }

    private void handleCrayonConfirm(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        Apostle apostle = sessionManager.getCurrentApostle(account.getId());
        if (apostle == null) return;

        apostleManager.confirmTrackUpdate(sessionManager.removeApostleTrackState(account.getId()));

        showApostlePanel(event, account, apostle);
    }

    private void handleCrayonReset(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        Apostle apostle = sessionManager.getCurrentApostle(account.getId());
        if (apostle == null) return;

        ApostleTrack track = apostleManager.findTrack(account, apostle);
        sessionManager.setApostleTrackState(account.getId(), track);

        showApostlePanel(event, account, apostle);
    }

    // ==================== Profile ====================

    private void showProfilePanel(ButtonInteractionEvent event, Account account) {
        event.deferEdit().queue(i ->
                i.editOriginalComponents(
                        panelBuilder.buildProfileComponent(account)
                ).useComponentsV2(true).queue()
        );

        if(sessionManager.getControlMessages(account.getId()) != null) {
            event.getHook().editMessageEmbedsById(
                    sessionManager.getControlMessages(account.getId()).getId(),
                    panelBuilder.buildMainMenuEmbed(event.getUser(), account)
            ).queue(m -> sessionManager.setControlMessages(account.getId(), m));
        }
    }

    private void handleProfile(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        showProfilePanel(event, account);
    }

    private void handleProfileUpdate(ButtonInteractionEvent event, String section) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());

        event.replyModal(
                Modal.create(modalPrefix + "profile_update_" + section, "Profile %s Update".formatted(section))
                        .addComponents(
                                Label.of("Text Input field for %s".formatted(section),
                                        TextInput.create(section, TextInputStyle.SHORT)
                                                .setRequired(true)
                                                .setValue(account.getDefaultText(section))
                                                .build()
                                )
                        ).build()
        ).queue();
    }

    private void handleProfileLeak(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        account.setLeak(!account.isLeak());

        factory.getRepos().accounts().save(account);

        showProfilePanel(event, account);
    }

    // ==================== DEEP SEARCH ====================

    private void handleDeepSearchModal(ButtonInteractionEvent event) {
        event.replyModal(
                Modal.create(modalPrefix + "apostle_switch_search", "Search Apostle")
                        .addComponents(
                                Label.of("Search by Name",
                                        TextInput.create("search_name", TextInputStyle.SHORT)
                                                .setPlaceholder("Apostle name...")
                                                .setRequired(false)
                                                .setMaxLength(100)
                                                .build()),
                                Label.of("Filter by Race",
                                        LabelByEnum.createCheckBoxGroup(
                                                "filter_race",
                                                ApostleRace.class
                                        ).setRequired(false).build()
                                ),
                                Label.of("Filter by Personality",
                                        LabelByEnum.createCheckBoxGroup(
                                                "filter_color",
                                                ApostleColor.class
                                        ).setRequired(false).build()
                                ),
                                Label.of("Filter by Position",
                                        LabelByEnum.createCheckBoxGroup(
                                                "filter_position",
                                                ApostlePosition.class
                                        ).setRequired(false).build()
                                )
                        ).build()
        ).queue();
    }

    // ==================== SUB-PANELS ====================

    private void handleImportExport(ButtonInteractionEvent event) {
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildImportExportPanel())
                        .build()
        ).queue();
    }

    private void handleDatabase(ButtonInteractionEvent event) {
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDatabasePanel())
                        .build()
        ).queue();
    }

    private void handleAdministrator(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        if (!account.isActionable(Operator.ADMIN)) {
            event.reply("❌ You need **Administrator** permission to access this panel!")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildAdministratorPanel())
                        .build()
        ).queue();
    }

    private void handleLogs(ButtonInteractionEvent event) {
        List<Log> recentLogs = factory.getRepos().logs().query()
                .orderByDesc("id")
                .limit(50)
                .list();
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildLogsPanel(recentLogs))
                        .build()
        ).queue();
    }

    // ==================== PUBLIC POST ====================

    private void handlePostApostle(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        event.getInteraction().getChannel().sendMessageEmbeds(
                panelBuilder.buildApostleEmbed(
                        sessionManager.getCurrentApostle(account.getId())
                )
        ).queue();

        event.reply("Success")
                .setEphemeral(true)
                .flatMap(InteractionHook::deleteOriginal)
                .queue();
    }

    private void handlePostTracker(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        event.getInteraction().getChannel().sendMessageEmbeds(
                panelBuilder.buildTrackEmbed(
                        account,
                        sessionManager.getCurrentApostle(account.getId())
                )
        ).queue();

        event.reply("Success")
                .setEphemeral(true)
                .flatMap(InteractionHook::deleteOriginal)
                .queue();
    }

    private void handlePostProfile(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        event.getInteraction().getChannel().sendMessageEmbeds(
                panelBuilder.buildMainMenuEmbed(event.getUser(), account)
        ).queue();

        event.reply("Success")
                .setEphemeral(true)
                .flatMap(InteractionHook::deleteOriginal)
                .queue();
    }

    // ==================== DELETE DATA ====================

    private void handleDeleteData(ButtonInteractionEvent event) {
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDeleteDataConfirmPanel())
                        .build()
        ).queue();
    }

    private void handleDeleteConfirm(ButtonInteractionEvent event, String buttonId) {
        if (buttonId.equals("delete_confirm")) {
            accountManager.deleteAllUserData(event.getUser().getId());

            event.getHook().editOriginal(
                    new MessageEditBuilder()
                            .setContent("✅ All your data has been permanently deleted. Use `/prickcal` to start fresh.")
                            .build()
            ).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
        } else {
            handleBackMain(event);
        }
    }

    // ==================== NAVIGATION ====================

    private void handleBackMain(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        event.deferEdit().queue(i ->
                i.editOriginalComponents(panelBuilder.buildMainMenuComponent(account))
                        .useComponentsV2(true)
                        .queue()
        );


        if (sessionManager.getControlMessages(account.getId()) != null) {
            event.getHook().editMessageEmbedsById(sessionManager.getControlMessages(account.getId()).getId(),
                    panelBuilder.buildMainMenuEmbed(event.getUser(), account)
            ).queue(m ->
                    sessionManager.clearUserSession(account)
                            .setControlMessages(account.getId(), m)
                            .setAccountCache(account.getUid(),  account)
            );
        }
    }
}
