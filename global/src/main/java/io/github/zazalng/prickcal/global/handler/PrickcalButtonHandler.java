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

import group.worldstandard.pudel.api.database.PluginRepository;
import io.github.zazalng.prickcal.global.builder.PanelBuilder;
import io.github.zazalng.prickcal.global.contract.operator.Action;
import io.github.zazalng.prickcal.global.contract.operator.Operator;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats;
import io.github.zazalng.prickcal.global.entities.*;
import io.github.zazalng.prickcal.global.exception.PrickcalEnum;
import io.github.zazalng.prickcal.global.exception.PrickcalException;
import io.github.zazalng.prickcal.global.session.SessionManager;
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroup;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles button interactions for the Prickcal plugin.
 */
public class PrickcalButtonHandler {

    private final String btnPrefix;
    private final String modalPrefix;
    private final PanelBuilder panelBuilder;
    private final SessionManager sessionManager;

    private PluginRepoProvider repos;

    public PrickcalButtonHandler(String btnPrefix, String modalPrefix,
                                 PanelBuilder panelBuilder, SessionManager sessionManager) {
        this.btnPrefix = btnPrefix;
        this.modalPrefix = modalPrefix;
        this.panelBuilder = panelBuilder;
        this.sessionManager = sessionManager;
    }

    public void setRepoProvider(PluginRepoProvider repos) {
        this.repos = repos;
    }

    public void handle(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        if (guild == null || member == null) return;
        if (repos == null) return;

        String userId = event.getUser().getId();
        String buttonId = event.getComponentId().substring(btnPrefix.length());

        if (buttonId.startsWith("consent_")) {
            handleConsent(event, userId);
        } else if (buttonId.equals("apostle")) {
            handleApostle(event, userId, guild);
        } else if (buttonId.startsWith("crayon_toggle_")) {
            handleCrayonToggle(event, userId);
        } else if (buttonId.equals("crayon_confirm")) {
            handleCrayonConfirm(event, userId);
        } else if (buttonId.equals("crayon_reset")) {
            handleCrayonReset(event, userId);
        } else if (buttonId.equals("switch_apostle")) {
            handleSwitchApostle(event, userId);
        } else if (buttonId.startsWith("deep_")) {
            handleDeepSearch(event, userId, buttonId);
        } else if (buttonId.equals("import_export")) {
            handleImportExport(event, userId);
        } else if (buttonId.equals("database")) {
            handleDatabase(event, userId);
        } else if (buttonId.equals("administrator")) {
            handleAdministrator(event, userId);
        } else if (buttonId.equals("logs")) {
            handleLogs(event, userId);
        } else if (buttonId.equals("public_post") || buttonId.equals("apostle_public_post")) {
            handlePublicPost(event, userId, guild, buttonId.startsWith("apostle_"));
        } else if (buttonId.equals("delete_data")) {
            handleDeleteData(event, userId);
        } else if (buttonId.startsWith("delete_")) {
            handleDeleteConfirm(event, userId, buttonId);
        } else if (buttonId.equals("deep_search_modal")) {
            handleDeepSearchModal(event);
        } else if (buttonId.equals("back_main")) {
            handleBackMain(event, userId);
        }
    }

    private void handleConsent(ButtonInteractionEvent event, String userId) {
        String action = event.getComponentId().substring(btnPrefix.length());
        switch (action) {
            case "consent_agree" -> {
                Account account = new Account();
                account.setUid(userId);
                repos.accounts().save(account);
                repos.logs().save(createLog(userId, "accounts", Action.CREATE, "an User has agreed to data usage consent"));

                event.editMessage(
                        new MessageEditBuilder()
                                .useComponentsV2(true)
                                .setComponents(panelBuilder.buildMainMenuComponent())
                                .build()
                ).queue();

                User discordUser = event.getUser();
                event.getHook().sendMessageEmbeds(
                        panelBuilder.buildMainMenuEmbed(discordUser, account, getDefaultCrayonStats(), null)
                ).setEphemeral(true).queue(msg -> {
                    sessionManager.putControlMessages(userId, Collections.singletonList(msg));
                });
            }
            case "consent_disagree" -> {
                event.reply("❌ Consent denied. Your data will not be tracked. Use `/prickcal` if you change your mind.")
                        .setEphemeral(true)
                        .queue(m -> m.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
            }
        }
    }

    // ==================== CONSENT ====================

    private void handleApostle(ButtonInteractionEvent event, String userId, Guild guild) {
        Account account = repos.accounts().query()
                .where("uid", userId)
                .findOne().orElse(null);
        if (account == null) return;

        Apostle apostle = repos.apostles().query().findOneOrThrow();

        if (apostle == null) {
            event.reply("❌ No apostles found in database.").setEphemeral(true).queue();
            return;
        }

        sessionManager.setCurrentApostle(userId, apostle);
        showApostlePanel(event, userId, apostle);
    }

    // ==================== APOSTLE ====================

    private void showApostlePanel(ButtonInteractionEvent event, String userId, Apostle apostle) {
        Account account = repos.accounts().query()
                .where("uid", userId)
                .findOne().orElse(null);
        if (account == null) return;

        ApostleTrack track = repos.apostleTrackers().query()
                .where("uid", userId)
                .where("apostle_id", apostle.getId())
                .findOne().orElse(null);

        CrayonLineUp lineUp = repos.crayonLineups()
                .findById(apostle.getCrayon())
                .orElseThrow(() ->
                        new PrickcalException(PrickcalEnum.INVALID_CRAYONLINEUP, String.valueOf(apostle.getCrayon()))
                );

        if (track != null) {
            List<Boolean> crayons = track.getCrayons();
            boolean[] state = new boolean[crayons.size()];
            for (int i = 0; i < crayons.size(); i++) {
                state[i] = crayons.get(i);
            }
            sessionManager.setCrayonToggleState(userId, state);
        }

        User discordUser = repos.getDiscordUser(userId);

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
                panelBuilder.buildApostleEmbed(displayUser, account, apostle, track, lineUp)
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

    private void handleCrayonToggle(ButtonInteractionEvent event, String userId) {
        String fullId = event.getComponentId().substring(btnPrefix.length());
        int index = Integer.parseInt(fullId.substring("crayon_toggle_".length()));
        boolean[] state = sessionManager.getCrayonToggleState(userId);
        if (state == null) return;

        state[index] = !state[index];
        sessionManager.setCrayonToggleState(userId, state);

        Apostle apostle = sessionManager.getCurrentApostle(userId);
        if (apostle == null) return;

        Account account = repos.accounts().query()
                .where("uid", userId)
                .findOne().orElse(null);
        if (account == null) return;

        ApostleTrack track = repos.apostleTrackers().query()
                .where("uid", userId)
                .where("apostleId", apostle.getId())
                .findOne().orElse(null);

        CrayonLineUp lineUp = apostle.getCrayon() != null
                ? repos.crayonLineups().query()
                .where("id", apostle.getCrayon())
                .findOne().orElse(null)
                : null;

        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildApostleComponent(
                                userId, apostle, track, lineUp, state))
                        .build()
        ).queue();
    }

    // ==================== CRAYON TOGGLE ====================

    private void handleCrayonConfirm(ButtonInteractionEvent event, String userId) {
        Apostle apostle = sessionManager.getCurrentApostle(userId);
        if (apostle == null) return;

        boolean[] state = sessionManager.getCrayonToggleState(userId);
        if (state == null) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < state.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(state[i]);
        }
        String crayonStr = sb.toString();

        ApostleTrack track = repos.apostleTrackers().query()
                .where("uid", userId)
                .where("apostle_id", apostle.getId())
                .findOne().orElse(null);

        if (track == null) {
            track = new ApostleTrack();
            track.setApostleId(apostle.getId());
            track.setUid(userId);
            track.setCurrentStar(apostle.getInit());
            track.setCrayon(crayonStr);
            repos.apostleTrackers().save(track);
            repos.logs().save(createLog(userId, "apostle_tracks", Action.CREATE,
                    "Created crayon track for " + apostle.getName()));
        } else {
            track.setCrayon(crayonStr);
            repos.apostleTrackers().save(track);
            repos.logs().save(createLog(userId, "apostle_tracks", Action.UPDATE,
                    "Updated crayon track for " + apostle.getName()));
        }

        event.reply("✅ Crayon data saved for **" + apostle.getName() + "**!")
                .setEphemeral(true)
                .queue(m -> m.deleteOriginal().queueAfter(3, TimeUnit.SECONDS));

        showApostlePanel(event, userId, apostle);
    }

    // ==================== CRAYON CONFIRM ====================

    private void handleCrayonReset(ButtonInteractionEvent event, String userId) {
        Apostle apostle = sessionManager.getCurrentApostle(userId);
        if (apostle == null) return;

        ApostleTrack track = repos.apostleTrackers().query()
                .where("uid", userId)
                .where("apostle_id", apostle.getId())
                .findOne().orElse(null);

        if (track != null) {
            List<Boolean> crayons = track.getCrayons();
            boolean[] state = new boolean[crayons.size()];
            for (int i = 0; i < crayons.size(); i++) {
                state[i] = crayons.get(i);
            }
            sessionManager.setCrayonToggleState(userId, state);
        }

        showApostlePanel(event, userId, apostle);
    }

    // ==================== CRAYON RESET ====================

    private void handleSwitchApostle(ButtonInteractionEvent event, String userId) {
        List<Apostle> allApostles = repos.apostles().query().list();
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildApostleListPanel(allApostles))
                        .build()
        ).queue();
    }

    // ==================== SWITCH APOSTLE ====================

    private void handleDeepSearchModal(ButtonInteractionEvent event) {
        event.replyModal(
                Modal.create(
                                modalPrefix + "switch_apostle_search", "Search Apostle")
                        .addComponents(
                                Label.of("Search by Name",
                                        net.dv8tion.jda.api.components.textinput.TextInput.create("search_name",
                                                        net.dv8tion.jda.api.components.textinput.TextInputStyle.SHORT)
                                                .setPlaceholder("Apostle name...")
                                                .setRequired(false)
                                                .setMaxLength(100)
                                                .build()),
                                Label.of("Filter by Personality",
                                        CheckboxGroup.create("filter_color")
                                                .addOption("Innocent", "0")
                                                .addOption("Composed", "1")
                                                .addOption("Mad", "2")
                                                .addOption("Vivacious", "3")
                                                .addOption("Depressed", "4")
                                                .setMaxValues(1)
                                                .build()),
                                Label.of("Filter by Position",
                                        CheckboxGroup.create("filter_position")
                                                .addOption("Front Column", "1")
                                                .addOption("Mid Column", "2")
                                                .addOption("Back Column", "3")
                                                .addOption("Round Robin", "0")
                                                .setMaxValues(1)
                                                .build())
                        ).build()
        ).queue();
    }

    // ==================== DEEP SEARCH ====================

    private void handleDeepSearch(ButtonInteractionEvent event, String userId, String buttonId) {
        switch (buttonId) {
            case "deep_name_input" -> {
                event.replyModal(
                        Modal.create(
                                        modalPrefix + "deep_name_search", "Search Apostle by Name")
                                .addComponents(
                                        Label.of("Apostle Name",
                                                TextInput.create("deep_name",
                                                                TextInputStyle.SHORT)
                                                        .setPlaceholder("e.g. Er, Ner, Aperil")
                                                        .setRequired(true)
                                                        .setMaxLength(100)
                                                        .build())
                                ).build()
                ).queue();
            }
            case "deep_prev" -> {
                int page = sessionManager.getDeepSearchPage(userId);
                sessionManager.setDeepSearchPage(userId, page - 1);
                refreshDeepSearch(event, userId);
            }
            case "deep_next" -> {
                int page = sessionManager.getDeepSearchPage(userId);
                sessionManager.setDeepSearchPage(userId, page + 1);
                refreshDeepSearch(event, userId);
            }
            case "deep_select" -> {
                List<Apostle> results = sessionManager.getDeepSearchResults(userId);
                if (results != null && results.size() == 1) {
                    Apostle selected = results.get(0);
                    sessionManager.setCurrentApostle(userId, selected);
                    showApostlePanel(event, userId, selected);
                }
            }
            case "deep_cancel" -> {
                sessionManager.removeDeepSearchResults(userId);
                sessionManager.removeDeepSearchPage(userId);
                handleSwitchApostle(event, userId);
            }
        }
    }

    private void refreshDeepSearch(ButtonInteractionEvent event, String userId) {
        List<Apostle> results = sessionManager.getDeepSearchResults(userId);
        int page = sessionManager.getDeepSearchPage(userId);
        if (results != null) {
            event.editMessage(
                    new MessageEditBuilder()
                            .useComponentsV2(true)
                            .setComponents(panelBuilder.buildDeepSearchPanel(userId, results, page, 10))
                            .build()
            ).queue();
        }
    }

    private void handleImportExport(ButtonInteractionEvent event, String userId) {
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildImportExportPanel())
                        .build()
        ).queue();
    }

    // ==================== SUB-PANELS ====================

    private void handleDatabase(ButtonInteractionEvent event, String userId) {
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDatabasePanel())
                        .build()
        ).queue();
    }

    private void handleAdministrator(ButtonInteractionEvent event, String userId) {
        Account account = repos.accounts().query()
                .where("uid", userId)
                .findOne().orElse(null);
        if (account == null) return;

        if (account.getOps() != Operator.ADMIN.getValue()) {
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

    private void handleLogs(ButtonInteractionEvent event, String userId) {
        List<Log> recentLogs = repos.logs().query()
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

    private void handlePublicPost(ButtonInteractionEvent event, String userId, Guild guild, boolean isApostle) {
        TextChannel channel = event.getChannel().asTextChannel();

        if (isApostle) {
            Apostle apostle = sessionManager.getCurrentApostle(userId);
            if (apostle == null) return;
            Account account = repos.accounts().query()
                    .where("uid", userId)
                    .findOne().orElse(null);
            if (account == null) return;
            User discordUser = repos.getDiscordUser(userId);
            ApostleTrack track = repos.apostleTrackers().query()
                    .where("uid", userId)
                    .where("apostle_id", apostle.getId())
                    .findOne().orElse(null);
            CrayonLineUp lineUp = apostle.getCrayon() != null
                    ? repos.crayonLineups().query()
                    .where("id", apostle.getCrayon())
                    .findOne().orElse(null)
                    : null;

            User displayUser = discordUser != null ? discordUser : event.getUser();
            channel.sendMessageEmbeds(
                    panelBuilder.buildApostleEmbed(displayUser, account, apostle, track, lineUp)
            ).queue(msg -> {
                sessionManager.putPublicPost(userId, channel.getId() + ":" + msg.getId());
                event.reply("✅ Apostle info posted publicly!").setEphemeral(true)
                        .queue(m -> m.deleteOriginal().queueAfter(3, TimeUnit.SECONDS));
            });
        } else {
            Account account = repos.accounts().query()
                    .where("uid", userId)
                    .findOne().orElse(null);
            if (account == null) return;

            User discordUser = repos.getDiscordUser(userId);
            User displayUser = discordUser != null ? discordUser : event.getUser();
            channel.sendMessageEmbeds(
                    panelBuilder.buildMainMenuEmbed(displayUser, account, getDefaultCrayonStats(), null)
            ).queue(msg -> {
                sessionManager.putPublicPost(userId, channel.getId() + ":" + msg.getId());
                event.reply("✅ Profile posted publicly!").setEphemeral(true)
                        .queue(m -> m.deleteOriginal().queueAfter(3, TimeUnit.SECONDS));
            });
        }
    }

    private void handleDeleteData(ButtonInteractionEvent event, String userId) {
        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildDeleteDataConfirmPanel())
                        .build()
        ).queue();
    }

    // ==================== DELETE DATA ====================

    private void handleDeleteConfirm(ButtonInteractionEvent event, String userId, String buttonId) {
        if (buttonId.equals("delete_confirm")) {
            repos.apostleTrackers().query()
                    .where("uid", userId)
                    .list().forEach(t -> repos.apostleTrackers().delete(t));

            repos.crayonRecords().query()
                    .where("uid", userId)
                    .list().forEach(c -> repos.crayonRecords().delete(c));

            repos.apostleRemarkables().query()
                    .where("uid", userId)
                    .list().forEach(r -> repos.apostleRemarkables().delete(r));

            repos.accounts().query()
                    .where("uid", userId)
                    .findOne().ifPresent(a -> repos.accounts().delete(a));

            repos.logs().save(createLog(userId, "accounts", Action.DELETE,
                    "User deleted all their data"));

            sessionManager.clearUserSession(userId);

            event.editMessage(
                    new MessageEditBuilder()
                            .setContent("✅ All your data has been permanently deleted. Use `/prickcal` to start fresh.")
                            .build()
            ).queue(m -> m.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
        } else {
            handleBackMain(event, userId);
        }
    }

    private void handleBackMain(ButtonInteractionEvent event, String userId) {
        sessionManager.clearUserSession(userId);
        Account account = repos.accounts().query()
                .where("uid", userId)
                .findOne().orElse(null);
        if (account == null) {
            event.editMessage(
                    new MessageEditBuilder()
                            .useComponentsV2(true)
                            .setComponents(panelBuilder.buildConsentPanel())
                            .build()
            ).queue();
            return;
        }

        User discordUser = repos.getDiscordUser(userId);
        User displayUser = discordUser != null ? discordUser : event.getUser();

        event.editMessage(
                new MessageEditBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildMainMenuComponent())
                        .build()
        ).queue();

        event.getHook().sendMessageEmbeds(
                panelBuilder.buildMainMenuEmbed(displayUser, account, getDefaultCrayonStats(), null)
        ).setEphemeral(true).queue(msg -> {
            List<Message> oldMsgs = sessionManager.getControlMessages(userId);
            if (oldMsgs != null) {
                for (Message m : oldMsgs) {
                    try {
                        m.delete().queue(null, _ -> {
                        });
                    } catch (Exception ignored) {
                    }
                }
            }
            sessionManager.putControlMessages(userId, Collections.singletonList(msg));
        });
    }

    // ==================== NAVIGATION ====================

    private List<CrayonStats> getDefaultCrayonStats() {
        return Arrays.asList(
                CrayonStats.ATK, CrayonStats.HP, CrayonStats.CRIT,
                CrayonStats.DEF, CrayonStats.CRES
        );
    }

    // ==================== HELPERS ====================

    private Log createLog(String uid, String table, Action action, String description) {
        Log log = new Log();
        log.setUid(uid);
        log.setTableName(table);
        log.setAction(action.name());
        log.setToString(description);
        return log;
    }

    public Log createLogEntry(String uid, String table, Action action, String description) {
        return createLog(uid, table, action, description);
    }

    public interface PluginRepoProvider {
        PluginRepository<Account> accounts();

        PluginRepository<Apostle> apostles();

        PluginRepository<ApostleRemarkable> apostleRemarkables();

        PluginRepository<ApostleTrack> apostleTrackers();

        PluginRepository<CrayonLineUp> crayonLineups();

        PluginRepository<CrayonRecord> crayonRecords();

        PluginRepository<Log> logs();

        User getDiscordUser(String uid);
    }
}
