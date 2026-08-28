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
import io.github.zazalng.prickcal.global.entities.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Plugin(
        name = "Prickcal [Global]",
        version = "0.0.1-indev",
        author = "Zazalng",
        description = "A plugin for personally tracking & collection Trickcal progression."
)
public class Prickcal {
    // ==================== HANDLER IDS (compile-time, used in annotations) ====================
    private static final String BTN_HANDLER = ":button:";
    private static final String MODAL_HANDLER = ":modal:";
    private static final String STRING_MENU_HANDLER = ":string:";
    private static final String ENTITY_MENU_HANDLER = ":entity:";
    private PluginContext ctx;

    // ==================== RUNTIME PREFIXED IDS (initialized in onEnable) ====================
    private String btnPrefix;
    private String modalPrefix;
    private String stringMenuPrefix;
    private String entityMenuPrefix;

    // ==================== Repo ====================
    private PluginRepository<Apostle> apostles;
    private PluginRepository<ApostleTrack> apostleTrackers;
    private PluginRepository<CrayonLineUp> crayonLineups;
    private PluginRepository<GiftAcquired> giftAcquires;
    private PluginRepository<GiftCode> giftCodes;
    private PluginRepository<Hashtag> hashTags;
    private PluginRepository<Log> logs;
    private PluginRepository<StageGearDrop> stageGears;
    private PluginRepository<Account> accounts;

    @OnEnable
    public void onEnable(PluginContext ctx) {
        this.ctx = ctx;
        // Initialization logic for runtime prefixed IDs
        String frontName = this.ctx.getDatabaseManager().getPluginId();
        this.btnPrefix = frontName + BTN_HANDLER;
        this.modalPrefix = frontName + MODAL_HANDLER;
        this.stringMenuPrefix = frontName + STRING_MENU_HANDLER;
        this.entityMenuPrefix = frontName + ENTITY_MENU_HANDLER;
        initializeDatabase(ctx.getDatabaseManager());
    }

    @OnDisable
    public void onDisable(PluginContext ctx) {
        this.ctx = null;
    }

    @OnShutdown
    public void onShutdown(PluginContext ctx) {
        this.ctx = null;
    }

    private void initializeDatabase(PluginDatabaseManager db){
        migrateDatabase(db);
        createRepositories(db);
    }

    private void migrateDatabase(PluginDatabaseManager db){
        db.migrate(0, _ -> {
            TableSchema tb = TableSchema.builder("accounts").fromEntity(Account.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("apostles").fromEntity(Apostle.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("apostle_reviews").fromEntity(ApostleRemarkable.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("apostle_tracks").fromEntity(ApostleTrack.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("crayon_line_ups").fromEntity(CrayonLineUp.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("gift_acquired").fromEntity(GiftAcquired.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("gift_codes").fromEntity(GiftCode.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("hash_tags").fromEntity(Hashtag.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("logs").fromEntity(Log.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("remarkable_records").fromEntity(RemarkableRecord.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("stage_gear_drops").fromEntity(StageGearDrop.class)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));
        });
    }

    private void createRepositories(PluginDatabaseManager db){
        apostles = db.getRepository("apostles", Apostle.class);
        apostleTrackers = db.getRepository("apostle_tracks", ApostleTrack.class);
        crayonLineups = db.getRepository("crayon_line_ups", CrayonLineUp.class);
        giftAcquires = db.getRepository("gift_acquired", GiftAcquired.class);
        giftCodes = db.getRepository("gift_codes", GiftCode.class);
        hashTags = db.getRepository("hash_tags", Hashtag.class);
        logs = db.getRepository("logs", Log.class);
        stageGears = db.getRepository("stage_gear_drops", StageGearDrop.class);
        accounts = db.getRepository("accounts", Account.class);
    }

    @SlashCommand(
            name = "prickal",
            description = "Open Control Panel for personal tracking.",
            nsfw = false,
            integrationTo = {IntegrationType.USER_INSTALL, IntegrationType.GUILD_INSTALL},
            integrationContext = {InteractionContextType.GUILD}
    )
    public void openMainControlPoint(SlashCommandInteractionEvent event){

    }

    @ContextMenu(
            baseName = "Prickcal",
            funcName = "View Record",
            type = Command.Type.USER
    )
    public void ephemeralViewRecord(UserContextInteractionEvent event){
        Guild guild = event.getGuild();
        User user = event.getUser();
        User target = event.getTarget();

        event.reply(
                new MessageCreateBuilder()
                        .useComponentsV2(true)
                        .setComponents()
                        .build()
        ).setEphemeral(true).queue();
    }
}