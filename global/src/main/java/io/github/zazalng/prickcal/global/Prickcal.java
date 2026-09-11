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
import group.worldstandard.pudel.api.database.TableSchema;
import io.github.zazalng.prickcal.global.builder.PanelBuilder;
import io.github.zazalng.prickcal.global.entities.*;
import io.github.zazalng.prickcal.global.handler.PrickcalButtonHandler;
import io.github.zazalng.prickcal.global.handler.PrickcalModalHandler;
import io.github.zazalng.prickcal.global.handler.PrickcalSelectMenuHandler;
import io.github.zazalng.prickcal.global.manager.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.Collections;
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
 *   <li><b>Managers</b> ({@link AccountManager}, {@link ApostleManager}, {@link PermissionManager}) — encapsulate
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

    // ==================== MANAGERS & SERVICES ====================
    private RepositoryProvider repoProvider;
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
        db.migrate(1, _ -> {
            TableSchema tb = TableSchema.builder("accounts").fromEntity(Account.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("apostles").fromEntity(Apostle.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("apostle_reviews").fromEntity(ApostleRemarkable.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("apostle_tracks").fromEntity(ApostleTrack.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("crayon_line_ups").fromEntity(CrayonLineUp.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("crayon_records").fromEntity(CrayonRecord.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("gift_acquired").fromEntity(GiftAcquired.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("gift_codes").fromEntity(GiftCode.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("hash_tags").fromEntity(Hashtag.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("logs").fromEntity(Log.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("remarkable_records").fromEntity(RemarkableRecord.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("stage_gear_drops").fromEntity(StageGearDrop.class).build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));
        });
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
        // -- Standalone services --
        this.panelBuilder = new PanelBuilder(btnPrefix, modalPrefix, stringMenuPrefix);

        // -- RepositoryProvider (standalone interface, not an inner class) --
        JDA jda = ctx.getJDA();
        this.repoProvider = createRepoProvider(jda);

        // -- Managers --
        this.factory = new ManagerFactory(ctx, repoProvider);

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
        String userId = event.getUser().getId();

        SessionManager sessionManager = factory.getManager(ManagersEnum.SESSION);
        AccountManager accountManager = factory.getManager(ManagersEnum.ACCOUNT);

        var optAccount = accountManager.findByUid(userId);
        if (optAccount.isEmpty()) {
            event.reply(
                    new MessageCreateBuilder()
                            .useComponentsV2(true)
                            .setComponents(panelBuilder.buildConsentPanel())
                            .build()
            ).setEphemeral(true).queue();
            return;
        }

        sessionManager.clearUserSession(userId);

        event.getInteraction().getHook().sendMessageEmbeds(
                panelBuilder.buildMainMenuEmbed(event.getUser(), optAccount.get(),
                        ApostleManager.defaultCrayonStats(), null
                )
        ).setEphemeral(true).queue(m -> sessionManager.putControlMessages(userId, Collections.singletonList(m)));

        event.reply(
                new MessageCreateBuilder()
                        .useComponentsV2(true)
                        .setComponents(panelBuilder.buildMainMenuComponent())
                        .build()
        ).setEphemeral(true).queue();
    }

    // ==================== CONTEXT MENU ====================

    @ContextMenu(
            baseName = "Prickcal",
            funcName = "View Record",
            type = Command.Type.USER
    )
    public void ephemeralViewRecord(UserContextInteractionEvent event) {
        User target = event.getTarget();
        String targetUid = target.getId();

        AccountManager accountManager = factory.getManager(ManagersEnum.ACCOUNT);

        var optAccount = accountManager.findByUid(targetUid);
        if (optAccount.isEmpty()) {
            event.reply("❌ This user doesn't have a Prickcal profile yet.")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        event.replyEmbeds(
                panelBuilder.buildMainMenuEmbed(target, optAccount.get(),
                        ApostleManager.defaultCrayonStats(), null
                )
        ).setEphemeral(true).queue();
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
}
