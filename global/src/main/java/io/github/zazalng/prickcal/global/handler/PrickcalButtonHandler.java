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
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleColorV2;
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
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.io.File;
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

    /**
     * Creates the button router for one plugin schema.
     *
     * @param btnPrefix    the prefix carried by every button custom ID that {@link PanelBuilder} emits
     * @param modalPrefix  the prefix used for the modal custom IDs this handler opens from a button
     * @param panelBuilder the builder that renders every panel this handler edits
     * @param factory      the manager factory used to resolve the session, account and apostle managers
     */
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

    /**
     * Routes a button interaction to the handler that owns its custom ID.
     * <p>
     * The custom ID is stripped of {@code btnPrefix} and dispatched by prefix: {@code consent_}, then
     * {@code apostle} (with {@code apostle_switching} and {@code apostle_increase_<true|false>} carved out),
     * then {@code crayon} (with {@code crayon_toggle_<index>}, {@code crayon_confirm} and {@code crayon_reset}
     * carved out), then {@code profile} (with {@code profile_leak} and {@code profile_<section>} carved out),
     * then the remaining exact ids {@code import_export}, {@code db}, {@code administrator}, {@code logs},
     * {@code delete_data}, {@code deep_search_modal} and {@code back_main}, plus the {@code post_*}
     * ({@code post_apostle}, {@code post_tracker}, {@code post_profile}) and {@code delete_*} families.
     * Guild-less and member-less interactions are ignored, and unrecognized ids are dropped without a reply.
     *
     * @param event the button interaction to dispatch
     */
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
        } else if (buttonId.startsWith("db")) {
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

    /**
     * Serves the consent notice buttons {@code consent_agree} and {@code consent_disagree}.
     * <p>
     * Agreeing creates the account through {@link AccountManager#createAccount(String, String)}, caches it
     * in the {@link SessionManager}, replaces the notice with a new ephemeral main menu and registers that
     * menu as the session's control message. Disagreeing deletes the notice and answers with an ephemeral
     * denial that removes itself after 5 seconds.
     *
     * @param event the {@code consent_*} button interaction
     */
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

    /**
     * Serves the {@code apostle} button by opening the crayon panel of a randomly picked apostle.
     * <p>
     * The pick is stored as the session's current apostle. Replies ephemerally with an error and returns
     * when the apostle table is empty, and does nothing when the user has no cached account.
     *
     * @param event the {@code apostle} button interaction
     */
    private void handleApostle(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        List<Apostle> all = apostleManager.listAll();
        if (all.isEmpty()) {
            event.reply("❌ No apostles found in database.").setEphemeral(true).queue();
            return;
        }

        Apostle apostle = all.get(new Random().nextInt(all.size()));
        sessionManager.setCurrentApostle(account.getId(), apostle);
        showApostlePanel(event, account, apostle);
    }

    /**
     * Redraws the crayon panel in place for the given apostle.
     * <p>
     * Edits the clicked message's components to {@link PanelBuilder#buildApostleComponent(Account, Apostle)}
     * and refreshes the session's control message embed to {@link PanelBuilder#buildTrackEmbed(Account, Apostle)}.
     *
     * @param event   the button interaction whose message holds the crayon panel
     * @param account the account whose session holds the control message
     * @param apostle the apostle the panel is being drawn for
     */
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

    /**
     * Serves the {@code apostle_switching} button by swapping the crayon panel for the apostle list panel.
     * <p>
     * The control message embed is re-rendered for the session's current apostle, but only when a control
     * message has already been registered for the account.
     *
     * @param event the {@code apostle_switching} button interaction
     */
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

    /**
     * Serves the {@code apostle_increase_true} and {@code apostle_increase_false} star buttons.
     * <p>
     * Applies {@link ApostleTrack#updateCurrentStar(Apostle, boolean)} to the staged track held in the session,
     * which clamps the star between the apostle's init and max, then redraws the panel. The change remains
     * staged until {@code crayon_confirm} persists it.
     *
     * @param event the {@code apostle_increase_*} button interaction
     * @param b     {@code true} to gain a star, {@code false} to lose one
     */
    private void handleApostleTrackStarIncreasing(ButtonInteractionEvent event, boolean b) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        ApostleTrack track = sessionManager.getApostleTrackState(account.getId());
        Apostle apostle = sessionManager.getCurrentApostle(account.getId());
        track.updateCurrentStar(apostle, b);
        showApostlePanel(event, account, apostle);
    }

    // ==================== CRAYON ====================

    /**
     * Serves the per-house {@code crayon_toggle_<index>} buttons.
     * <p>
     * Flips the acquired flag at that index of the staged track, raises the staged star to the apostle's
     * init star when the first crayon becomes acquired, and redraws the panel. The edit stays staged until
     * {@code crayon_confirm} persists it.
     *
     * @param event    the {@code crayon_toggle_*} button interaction
     * @param buttonId the custom ID with {@code btnPrefix} and {@code crayon_toggle_} already stripped, i.e. the house index
     */
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

    /**
     * Serves the {@code crayon_confirm} button by persisting the staged crayon grid.
     * <p>
     * Removes the staged {@link ApostleTrack} from the session, hands it to
     * {@link ApostleManager#confirmTrackUpdate(ApostleTrack)} and redraws the panel from the saved state.
     *
     * @param event the {@code crayon_confirm} button interaction
     */
    private void handleCrayonConfirm(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        Apostle apostle = sessionManager.getCurrentApostle(account.getId());
        if (apostle == null) return;

        apostleManager.confirmTrackUpdate(sessionManager.removeApostleTrackState(account.getId()));

        showApostlePanel(event, account, apostle);
    }

    /**
     * Serves the {@code crayon_reset} button by discarding the staged edits.
     * <p>
     * Replaces the staged track in the session with the currently persisted one from
     * {@link ApostleManager#findTrack(Account, Apostle)}, then redraws the panel.
     *
     * @param event the {@code crayon_reset} button interaction
     */
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

    /**
     * Redraws the profile panel in place.
     * <p>
     * Edits the clicked message's components to {@link PanelBuilder#buildProfileComponent(Account)} and, when a
     * control message is registered, refreshes its embed to {@link PanelBuilder#buildMainMenuEmbed(net.dv8tion.jda.api.entities.User, Account)}
     * so the summary stays in sync with the edited profile.
     *
     * @param event   the button interaction whose message holds the profile panel
     * @param account the account being displayed and edited
     */
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

    /**
     * Serves the {@code profile} button by opening the profile panel.
     * <p>
     * The cached account is passed straight through without a null check.
     *
     * @param event the {@code profile} button interaction
     */
    private void handleProfile(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        showProfilePanel(event, account);
    }

    /**
     * Serves the {@code profile_<section>} buttons by opening the edit modal for that section.
     * <p>
     * The modal is a single required short text input pre-filled with the section's current value from
     * {@link Account#getDefaultText(String)}; submission is handled by {@link PrickcalModalHandler}.
     *
     * @param event   the {@code profile_*} button interaction
     * @param section the section key ({@code ign}, {@code code} or {@code format}) with {@code profile_} already stripped
     */
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

    /**
     * Serves the {@code profile_leak} button by flipping the account's leak-content visibility.
     * <p>
     * The toggled {@link Account} is saved straight to the repository, then the profile panel is redrawn.
     *
     * @param event the {@code profile_leak} button interaction
     */
    private void handleProfileLeak(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        account.setLeak(!account.isLeak());

        factory.getRepos().accounts().save(account);

        showProfilePanel(event, account);
    }

    // ==================== DEEP SEARCH ====================

    /**
     * Serves the {@code deep_search_modal} button by opening the apostle search modal.
     * <p>
     * The modal carries an optional name text input plus optional race, personality and position checkbox
     * groups produced by {@link LabelByEnum#createCheckBoxGroup(String, Class)}. Submission is handled by
     * {@link PrickcalModalHandler} under the {@code apostle_switch_search} id.
     *
     * @param event the {@code deep_search_modal} button interaction
     */
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
                                                ApostleColorV2.class
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

    /**
     * Serves the {@code import_export} button by editing the message to the import/export panel in place.
     * <p>
     * That panel is currently a placeholder; it carries no session state.
     *
     * @param event the {@code import_export} button interaction
     */
    private void handleImportExport(ButtonInteractionEvent event) {
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildImportExportPanel())
                        .build()
        ).queue();
    }

    /**
     * Serves the {@code database} button by editing the message to the database panel in place.
     * <p>
     * That panel is currently a placeholder; it carries no session state.
     *
     * @param event the {@code database} button interaction
     */
    private void handleDatabase(ButtonInteractionEvent event) {
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDatabasePanel(sessionManager.getAccountCache(event.getUser().getId())))
                        .build()
        ).queue();
    }

    /**
     * Serves the {@code administrator} button, enforcing the {@link Operator#ADMIN} permission first.
     * <p>
     * Answers with an ephemeral refusal that deletes itself after 5 seconds when the cached account is not
     * actionable at admin level, otherwise edits the message to {@link PanelBuilder#buildAdministratorPanel()}.
     *
     * @param event the {@code administrator} button interaction
     */
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

    /**
     * Serves the {@code logs} button by editing the message to the public logs panel.
     * <p>
     * Reads the 50 most recent {@link Log} rows, newest id first. No permission check is applied because
     * these logs are part of the public-resource transparency notice.
     *
     * @param event the {@code logs} button interaction
     */
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

    /**
     * Serves the {@code post_apostle} button by posting the static apostle embed to the channel.
     * <p>
     * Uses the session's current apostle.
     *
     * @param event the {@code post_apostle} button interaction
     */
    private void handlePostApostle(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        Apostle apostle = sessionManager.getCurrentApostle(account.getId());

        event.getInteraction().getChannel().sendMessageEmbeds(
                panelBuilder.buildApostleEmbed(
                        apostle
                )
        ).queue();

        showApostlePanel(event, account, sessionManager.getCurrentApostle(account.getId()));
    }

    /**
     * Serves the {@code post_tracker} button by posting the caller's tracker embed to the channel.
     * <p>
     * Built from the session's current apostle.
     *
     * @param event the {@code post_tracker} button interaction
     */
    private void handlePostTracker(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        Apostle apostle = sessionManager.getCurrentApostle(account.getId());

        event.getInteraction().getChannel().sendMessageEmbeds(
                panelBuilder.buildTrackEmbed(
                        account,
                        apostle
                )
        ).queue();

        showApostlePanel(event, account, apostle);
    }

    /**
     * Serves the {@code post_profile} button by posting the caller's profile summary embed to the channel.
     *
     * @param event the {@code post_profile} button interaction
     */
    private void handlePostProfile(ButtonInteractionEvent event) {
        Account account = sessionManager.getAccountCache(event.getUser().getId());
        if (account == null) return;

        File template = null;
        if (accountManager.hasUserTemplate(account)) {
            template = accountManager.fillUserTemplate(account);
        }

        if (template != null) {
            event.getInteraction().getChannel().sendFiles(
                    AttachedFile.fromData(template)
            ).queue();
        } else {
            event.getInteraction().getChannel().sendMessageEmbeds(
                    panelBuilder.buildMainMenuEmbed(event.getUser(), account)
            ).queue();
        }

        showProfilePanel(event, account);
    }

    // ==================== DELETE DATA ====================

    /**
     * Serves the {@code delete_data} button by editing the message to the deletion confirmation panel.
     * <p>
     * Nothing is deleted here; the panel only offers {@code delete_confirm} and {@code delete_cancel}.
     *
     * @param event the {@code delete_data} button interaction
     */
    private void handleDeleteData(ButtonInteractionEvent event) {
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDeleteDataConfirmPanel())
                        .build()
        ).queue();
    }

    /**
     * Serves the {@code delete_*} family: {@code delete_confirm} erases the caller's data, anything else cancels.
     * <p>
     * Confirmation runs {@link AccountManager#deleteAllUserData(String)} and then replaces the message with a
     * notice that deletes itself after 5 seconds. Any other {@code delete_*} id, such as {@code delete_cancel},
     * is routed to {@link #handleBackMain(ButtonInteractionEvent)}.
     *
     * @param event    the {@code delete_*} button interaction
     * @param buttonId the custom ID with {@code btnPrefix} already stripped, compared against {@code delete_confirm}
     */
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

    /**
     * Serves the {@code back_main} button by returning to the main menu.
     * <p>
     * Edits the clicked message back to {@link PanelBuilder#buildMainMenuComponent(Account)}, refreshes the
     * control message embed, and clears the cached session so no staged crayon, apostle or search state
     * survives the navigation.
     *
     * @param event the {@code back_main} button interaction
     */
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
