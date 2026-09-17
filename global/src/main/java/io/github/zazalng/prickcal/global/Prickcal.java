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
package io.github.zazalng.prickcal.global;

import group.worldstandard.pudel.api.PluginContext;
import group.worldstandard.pudel.api.annotation.*;
import group.worldstandard.pudel.api.database.PluginDatabaseManager;
import group.worldstandard.pudel.api.database.PluginRepository;
import io.github.zazalng.prickcal.global.builder.PanelBuilder;
import io.github.zazalng.prickcal.global.entities.*;
import io.github.zazalng.prickcal.global.handler.PrickcalButtonHandler;
import io.github.zazalng.prickcal.global.handler.PrickcalModalHandler;
import io.github.zazalng.prickcal.global.handler.PrickcalSelectMenuHandler;
import io.github.zazalng.prickcal.global.manager.*;
import io.github.zazalng.prickcal.global.util.CrayonFormatParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Prickcal [Global] — A Trickcal's procession tracker for Pudel Bot.
 *
 * <p>Provides Components V2 control panels for personally tracking
 * Trickcal progression including crayon records, apostle tracking,
 * and community features.
 *
 * <p>Architecture:
 * <ul>
 *   <li><b>Managers</b> ({@link AccountManager}, {@link ApostleManager}, etc.) — encapsulate
 *       all business logic, repository access, and audit logging.</li>
 *   <li><b>Handlers</b> — thin JDA event routers that delegate to managers.</li>
 *   <li><b>SessionManager</b> — ephemeral per-user state.</li>
 *   <li><b>PanelBuilder</b> — pure UI construction, no business logic.</li>
 * </ul>
 */
@Plugin(
        name = "Prickcal [Global]",
        version = "0.0.1-indev",
        author = "Zazalng",
        description = "A plugin for personally tracking & collection Trickcal progression."
)
public class Prickcal {
    // ==================== HANDLER IDS ====================
    private static final String BTN_HANDLER = ":button:";
    private static final String MODAL_HANDLER = ":modal:";
    private static final String STRING_MENU_HANDLER = ":string:";

    // ==================== RUNTIME PREFIXES ====================
    private String btnPrefix;
    private String modalPrefix;
    private String stringMenuPrefix;

    // ==================== CONTEXT ====================
    private PluginContext ctx;

    // ==================== REPOSITORIES ====================
    private PluginRepository<Account> accounts;
    private PluginRepository<Apostle> apostles;
    private PluginRepository<ApostleRemarkable> apostleRemarkables;
    private PluginRepository<ApostleTrack> apostleTrackers;
    private PluginRepository<CrayonLineUp> crayonLineups;
    private PluginRepository<CrayonRecord> crayonRecords;
    private PluginRepository<GiftAcquired> giftAcquires;
    private PluginRepository<GiftCode> giftCodes;
    private PluginRepository<Hashtag> hashTags;
    private PluginRepository<Log> logs;
    private PluginRepository<RemarkableRecord> remarkableRecords;
    private PluginRepository<StageGearDrop> stageGears;

    private ManagerFactory factory;
    private PanelBuilder panelBuilder;
    private PrickcalButtonHandler prickcalButtonHandler;
    private PrickcalModalHandler prickcalModalHandler;
    private PrickcalSelectMenuHandler prickcalSelectMenuHandler;

    // ==================== LIFECYCLE ====================

    @OnEnable
    public void onEnable(PluginContext ctx) {
        this.ctx = ctx;

        String prefix = ctx.getDatabaseManager().getSchemaName();
        this.btnPrefix = prefix + BTN_HANDLER;
        this.modalPrefix = prefix + MODAL_HANDLER;
        this.stringMenuPrefix = prefix + STRING_MENU_HANDLER;

        initializeDatabase(ctx.getDatabaseManager());
        initializeServices();

        ctx.log("info", "%s (v%s) has initialized on '%s'".formatted(
                ctx.getInfo().getName(), ctx.getInfo().getVersion(), ctx.getPudel().getUserAgent())
        );
    }

    @OnDisable
    public void onDisable(PluginContext ctx) {
        if (factory != null) {
            factory.shutdownAllManagers();
        }
        this.ctx = null;
    }

    @OnShutdown
    public boolean onShutdown(PluginContext ctx) {
        try {
            if (factory != null) {
                factory.shutdownAllManagers();
            }
            ctx.log("info", "%s (v%s) graceful shutdown on '%s'".formatted(
                    ctx.getInfo().getName(), ctx.getInfo().getVersion(), ctx.getPudel().getUserAgent())
            );
            this.ctx = null;
            return true;
        } catch (Exception e) {
            ctx.log("error", "Unable to shutdown gracefully: '%s'".formatted(e.getMessage()), e);
            return false;
        }
    }

    // ==================== INITIALIZATION ====================

    private void initializeDatabase(PluginDatabaseManager db) {
        migrateDatabase(db);
        createRepositories(db);
    }

    private void migrateDatabase(PluginDatabaseManager db) {
        db.autoMigrate(Account.class,
                Apostle.class,
                ApostleRemarkable.class,
                ApostleTrack.class,
                CrayonLineUp.class,
                CrayonRecord.class,
                GiftAcquired.class,
                GiftCode.class,
                Hashtag.class,
                Log.class,
                RemarkableRecord.class,
                StageGearDrop.class
        );
    }

    private void createRepositories(PluginDatabaseManager db) {
        accounts = db.getRepository(Account.class);
        apostles = db.getRepository(Apostle.class);
        apostleRemarkables = db.getRepository(ApostleRemarkable.class);
        apostleTrackers = db.getRepository(ApostleTrack.class);
        crayonLineups = db.getRepository(CrayonLineUp.class);
        crayonRecords = db.getRepository(CrayonRecord.class);
        giftAcquires = db.getRepository(GiftAcquired.class);
        giftCodes = db.getRepository(GiftCode.class);
        hashTags = db.getRepository(Hashtag.class);
        logs = db.getRepository(Log.class);
        remarkableRecords = db.getRepository(RemarkableRecord.class);
        stageGears = db.getRepository(StageGearDrop.class);
    }

    private void initializeServices() {
        // -- RepositoryProvider (standalone interface, not an inner class) --
        JDA jda = ctx.getJDA();
        // ==================== MANAGERS & SERVICES ====================
        RepositoryProvider repoProvider = createRepoProvider(jda);

        // -- Managers --
        this.factory = new ManagerFactory(ctx, repoProvider);

        // -- Standalone services --
        this.panelBuilder = new PanelBuilder(factory, btnPrefix, modalPrefix, stringMenuPrefix);

        // -- Handlers (thin routers) --
        this.prickcalButtonHandler = new PrickcalButtonHandler(
                btnPrefix, modalPrefix, panelBuilder, factory);
        this.prickcalModalHandler = new PrickcalModalHandler(
                modalPrefix, panelBuilder, factory);
        this.prickcalSelectMenuHandler = new PrickcalSelectMenuHandler(
                stringMenuPrefix, panelBuilder, factory);
    }

    private RepositoryProvider createRepoProvider(JDA jda) {
        return new RepositoryProvider() {
            @Override
            public PluginRepository<Account> accounts() {
                return accounts;
            }

            @Override
            public PluginRepository<Apostle> apostles() {
                return apostles;
            }

            @Override
            public PluginRepository<ApostleRemarkable> apostleRemarkables() {
                return apostleRemarkables;
            }

            @Override
            public PluginRepository<ApostleTrack> apostleTrackers() {
                return apostleTrackers;
            }

            @Override
            public PluginRepository<CrayonLineUp> crayonLineups() {
                return crayonLineups;
            }

            @Override
            public PluginRepository<CrayonRecord> crayonRecords() {
                return crayonRecords;
            }

            @Override
            public PluginRepository<GiftAcquired> giftAcquires() {
                return giftAcquires;
            }

            @Override
            public PluginRepository<GiftCode> giftCodes() {
                return giftCodes;
            }

            @Override
            public PluginRepository<Hashtag> hashTags() {
                return hashTags;
            }

            @Override
            public PluginRepository<Log> logs() {
                return logs;
            }

            @Override
            public PluginRepository<RemarkableRecord> remarkableRecords() {
                return remarkableRecords;
            }

            @Override
            public PluginRepository<StageGearDrop> stageGears() {
                return stageGears;
            }

            @Override
            public User getDiscordUser(String uid) {
                if (jda == null) return null;
                User user = jda.getUserById(uid);
                if (user == null) {
                    try {
                        user = jda.retrieveUserById(uid).complete();
                    } catch (Exception ignored) {
                    }
                }
                return user;
            }
        };
    }

    // ==================== SLASH COMMAND ====================

    @SlashCommand(
            name = "prickcal",
            description = "Open Control Panel for personal tracking.",
            nsfw = false,
            integrationTo = {IntegrationType.USER_INSTALL, IntegrationType.GUILD_INSTALL},
            integrationContext = {InteractionContextType.GUILD, InteractionContextType.BOT_DM}
    )
    public void openMainControlPoint(SlashCommandInteractionEvent event) {
        String uid = event.getUser().getId();

        AccountManager accountManager = factory.getManager(ManagersEnum.ACCOUNT);
        SessionManager sessionManager = factory.getManager(ManagersEnum.SESSION);

        Optional<Account> account = accountManager.findByUid(uid);
        if (account.isEmpty()) {
            event.replyComponents(panelBuilder.buildConsentPanel()).setEphemeral(true).queue();
            return;
        }

        sessionManager.clearUserSession(account.get());

        event.getInteraction().getHook().sendMessageEmbeds(
                panelBuilder.buildMainMenuEmbed(event.getUser(), account.get())
        ).setEphemeral(true).queue(m -> sessionManager.setControlMessages(account.get().getId(), m));

        event.deferReply(true).queue(i ->
                i.sendMessageComponents(panelBuilder.buildMainMenuComponent())
                        .useComponentsV2(true)
                        .setEphemeral(true)
                        .queue()
        );
    }

    // ==================== CONTEXT MENU ====================

    @ContextMenu(
            baseName = "Prickcal",
            funcName = "View Record",
            type = Command.Type.USER,
            nsfw = false,
            integrationTo = {IntegrationType.GUILD_INSTALL, IntegrationType.USER_INSTALL},
            integrationContext = {InteractionContextType.GUILD}
    )
    public void ephemeralViewRecord(UserContextInteractionEvent event) {
        AccountManager accountManager = factory.getManager(ManagersEnum.ACCOUNT);
        Optional<Account> account = accountManager.findByUid(event.getUser().getId());
        if (account.isEmpty()) {
            event.replyComponents(panelBuilder.buildConsentPanel()).setEphemeral(true).queue();
            return;
        }

        User target = event.getTarget();
        String targetUid = target.getId();

        Optional<Account> optAccount = accountManager.findByUid(targetUid);
        if (optAccount.isEmpty()) {
            event.reply("❌ This user doesn't have a Prickcal profile yet.")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        event.replyEmbeds(
                panelBuilder.buildMainMenuEmbed(target, optAccount.get())
        ).setEphemeral(true).queue();
    }

    @ContextMenu(
            baseName = "Prickcal",
            funcName = "Crayon Record",
            type = Command.Type.MESSAGE,
            nsfw = false,
            integrationTo = {IntegrationType.GUILD_INSTALL, IntegrationType.USER_INSTALL},
            integrationContext = {
                    InteractionContextType.GUILD,
                    InteractionContextType.BOT_DM,
                    InteractionContextType.PRIVATE_CHANNEL
            }
    )
    public void CrayonRecording(MessageContextInteractionEvent event) {
        AccountManager accountManager = factory.getManager(ManagersEnum.ACCOUNT);

        Optional<Account> account = accountManager.findByUid(event.getUser().getId());

        if (account.isEmpty()) {
            event.replyComponents(panelBuilder.buildConsentPanel())
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!event.getUser().getId().equalsIgnoreCase(event.getTarget().getAuthor().getId())) {
            event.reply("The user running the interaction is not the author of the target message")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        String format = account.get().getCrayonFormat();
        String message = event.getTarget().getContentStripped();

        Optional<CrayonFormatParser.Result> result =
                CrayonFormatParser.parse(format, message);

        if (result.isEmpty()) {
            reject(
                    event,
                    "Incorrect format of User '%s'".formatted(format)
            );
            return;
        }

        Map<String, String> values = result.get().values();

        // Make sure the user's format actually contains all required values.
        if (!values.containsKey("%dd")
                || !values.containsKey("%dm")
                || !values.containsKey("%dy")
                || !values.containsKey("%cs")
                || !values.containsKey("%ca")) {

            reject(
                    event,
                    "Your Crayon Format must contain %%dd, %%dm, %%dy, %%cs and %%ca."
            );
            return;
        }

        try {
            int day = Integer.parseInt(values.get("%dd"));
            int month = Integer.parseInt(values.get("%dm"));

            String yearValue = values.get("%dy");
            int year = yearValue.length() == 2
                    ? 2000 + Integer.parseInt(yearValue)
                    : Integer.parseInt(yearValue);

            int candySpent = Integer.parseInt(values.get("%cs"));
            int crayonAcquired = Integer.parseInt(values.get("%ca"));

            if (candySpent < 20) {
                reject(
                        event,
                        "Candy Spend input '%d' does not reach the minimum of 20."
                                .formatted(candySpent)
                );
                return;
            }

            if (candySpent % 20 != 0) {
                reject(
                        event,
                        "Candy Spend '%d' is not divisible by 20."
                                .formatted(candySpent)
                );
                return;
            }

            LocalDate recordDate = LocalDate.of(year, month, day);

            CrayonRecord row = new CrayonRecord();

            row.setUid(account.get().getId());

            if (!event.getTarget().getAttachments().isEmpty()) {
                row.setImgUrl(
                        event.getTarget()
                                .getAttachments()
                                .getFirst()
                                .getUrl()
                );
            }

            row.setSpent(candySpent);
            row.setCrayon(crayonAcquired);
            row.setRecordDate(recordDate);

            factory.getRepos()
                    .crayonRecords()
                    .save(row);

            event.getTarget()
                    .removeReaction(
                            Emoji.fromUnicode("❌"),
                            event.getJDA().getSelfUser()
                    )
                    .queue();

            event.getTarget()
                    .addReaction(Emoji.fromUnicode("✅"))
                    .queue();

            event.reply("Success")
                    .setEphemeral(true)
                    .flatMap(InteractionHook::deleteOriginal)
                    .queue();
        } catch (DateTimeException ex) {
            reject(
                    event,
                    "Date dd (%s), dm (%s), dy (%s) cannot be parsed into a valid LocalDate."
                            .formatted(
                                    values.get("%dd"),
                                    values.get("%dm"),
                                    values.get("%dy")
                            )
            );

        } catch (NumberFormatException ex) {
            reject(
                    event,
                    "NumberFormatException: '%s'"
                            .formatted(ex.getMessage())
            );
        }
    }

    // ==================== HANDLER ROUTERS ====================

    @group.worldstandard.pudel.api.annotation.ButtonHandler(":button:")
    public void handleButton(ButtonInteractionEvent event) {
        prickcalButtonHandler.handle(event);
    }

    @group.worldstandard.pudel.api.annotation.ModalHandler(":modal:")
    public void handleModal(ModalInteractionEvent event) {
        prickcalModalHandler.handle(event);
    }

    @group.worldstandard.pudel.api.annotation.SelectMenuHandler(":string:")
    public void handleSelectMenu(StringSelectInteractionEvent event) {
        prickcalSelectMenuHandler.handle(event);
    }

    // ==================== HELPER ====================

    private void reject(
            MessageContextInteractionEvent event,
            String reason
    ) {
        event.getTarget()
                .addReaction(Emoji.fromUnicode("❌"))
                .queue();

        event.getUser()
                .openPrivateChannel()
                .queue(channel ->
                        channel.sendMessage(
                                "Operation Error: " + reason
                        ).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS))
                );
    }
}
