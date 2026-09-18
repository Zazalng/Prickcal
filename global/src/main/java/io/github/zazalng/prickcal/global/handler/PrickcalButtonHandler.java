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
import java.util.Optional;
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
                handleSwitchApostle(event);
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
                event.getHook().deleteOriginal().queue();

                event.getInteraction().getHook().sendMessageEmbeds(
                        panelBuilder.buildMainMenuEmbed(event.getUser(), account)
                ).setEphemeral(true).queue(m -> sessionManager.setControlMessages(account.getId(), m));

                event.deferReply(true).queue(i ->
                        i.sendMessageComponents(panelBuilder.buildMainMenuComponent())
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
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) return;

        // Pick the first apostle as default
        List<Apostle> all = apostleManager.listAll();
        if (all.isEmpty()) {
            event.reply("❌ No apostles found in database.").setEphemeral(true).queue();
            return;
        }
        Apostle apostle = all.get(new Random().nextInt(all.size()));
        sessionManager.setCurrentApostle(account.get().getId(), apostle);
        showApostlePanel(event, account.get(), apostle);
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

    // ==================== CRAYON TOGGLE ====================

    private void handleCrayonToggle(ButtonInteractionEvent event, String buttonId) {
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) return;

        Apostle apostle = sessionManager.getCurrentApostle(account.get().getId());
        if (apostle == null) return;

        int index = Integer.parseInt(buttonId.substring("crayon_toggle_".length()));
        ApostleTrack track = sessionManager.getApostleTrackState(account.get().getId());
        List<Boolean> state = track.getCrayons();
        if (state == null) return;

        state.set(index, !state.get(index));
        if (state.contains(true) && track.getCurrentStar() < apostle.getInit()) track.setCurrentStar(apostle.getInit());

        sessionManager.setApostleTrackState(account.get().getId(), track.updateCrayon(state));

        showApostlePanel(event, account.get(), apostle);
    }

    // ==================== CRAYON CONFIRM ====================

    private void handleCrayonConfirm(ButtonInteractionEvent event) {
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) return;

        Apostle apostle = sessionManager.getCurrentApostle(account.get().getId());
        if (apostle == null) return;

        apostleManager.confirmTrackUpdate(sessionManager.removeApostleTrackState(account.get().getId()));

        showApostlePanel(event, account.get(), apostle);
    }

    // ==================== CRAYON RESET ====================

    private void handleCrayonReset(ButtonInteractionEvent event) {
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) return;

        Apostle apostle = sessionManager.getCurrentApostle(account.get().getId());
        if (apostle == null) return;

        ApostleTrack track = apostleManager.findTrack(account.get(), apostle);
        sessionManager.setApostleTrackState(account.get().getId(), track);

        showApostlePanel(event, account.get(), apostle);
    }

    // ==================== SWITCH APOSTLE ====================

    private void handleSwitchApostle(ButtonInteractionEvent event) {
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) return;

        event.deferEdit().queue(i ->
                i.editOriginalComponents(
                    panelBuilder.buildApostleListPanel(account.get()))
                        .useComponentsV2()
                        .queue()
        );

        if (sessionManager.getControlMessages(account.get().getId()) != null) {
            event.getHook().editMessageEmbedsById(sessionManager.getControlMessages(account.get().getId()).getId(),
                    panelBuilder.buildTrackEmbed(account.get(), sessionManager.getCurrentApostle(account.get().getId()))
            ).queue(m -> sessionManager.setControlMessages(account.get().getId(), m));
        }
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
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) return;

        if (!account.get().isActionable(Operator.ADMIN)) {
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
        var optAccount = accountManager.findByUid(event.getUser().getId());
        if (optAccount.isEmpty()) return;

        event.getInteraction().getChannel().sendMessageEmbeds(panelBuilder.buildApostleEmbed(sessionManager.getCurrentApostle(optAccount.get().getId()))).queue();

        event.reply("Success")
                .setEphemeral(true)
                .flatMap(InteractionHook::deleteOriginal)
                .queue();
    }

    private void handlePostTracker(ButtonInteractionEvent event) {
        var optAccount = accountManager.findByUid(event.getUser().getId());
        if (optAccount.isEmpty()) return;

        event.getInteraction().getChannel().sendMessageEmbeds(panelBuilder.buildTrackEmbed(optAccount.get(), sessionManager.getCurrentApostle(optAccount.get().getId()))).queue();

        event.reply("Success")
                .setEphemeral(true)
                .flatMap(InteractionHook::deleteOriginal)
                .queue();
    }

    private void handlePostProfile(ButtonInteractionEvent event) {
        var optAccount = accountManager.findByUid(event.getUser().getId());
        if (optAccount.isEmpty()) return;

        event.getInteraction().getChannel().sendMessageEmbeds(panelBuilder.buildMainMenuEmbed(event.getUser(), optAccount.get())).queue();

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
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) return;

        event.deferEdit().queue(i ->
                i.editOriginalComponents(panelBuilder.buildMainMenuComponent())
                        .useComponentsV2(true)
                        .queue()
        );


        if (sessionManager.getControlMessages(account.get().getId()) != null) {
            event.getHook().editMessageEmbedsById(sessionManager.getControlMessages(account.get().getId()).getId(),
                    panelBuilder.buildMainMenuEmbed(event.getUser(), account.get())
            ).queue(m -> sessionManager.clearUserSession(account.get()).setControlMessages(account.get().getId(), m));
        }
    }
}
