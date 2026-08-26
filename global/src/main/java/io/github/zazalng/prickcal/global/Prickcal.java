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
import group.worldstandard.pudel.api.database.ColumnType;
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
            TableSchema tb = TableSchema.builder("apostle")
                    .column("name", ColumnType.STRING, false, "Unrecognized")
                    .column("pic", ColumnType.STRING, true)
                    .column("init", ColumnType.SMALLINT, 1, false, "0")
                    .column("crayon", ColumnType.BIGINT, false, "0")
                    .column("race", ColumnType.SMALLINT, 1, false, "0")
                    .column("elyde", ColumnType.BOOLEAN, false, "false")
                    .column("hash_tag", ColumnType.STRING, true)
                    .column("release_date", ColumnType.DATE, false)
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("apostle_track")
                    .column("apostle_id", ColumnType.BIGINT, false, "0")
                    .column("uid", ColumnType.STRING, false)
                    .column("current_star", ColumnType.SMALLINT, false, "0")
                    .column("crayon", ColumnType.STRING, false)
                    .uniqueIndex("apostle_id", "uid")
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("crayon_line_up")
                    .column("house_1a", ColumnType.SMALLINT, false, "0")
                    .column("house_1b", ColumnType.SMALLINT, false, "0")
                    .column("house_2a", ColumnType.SMALLINT, false, "0")
                    .column("house_2b", ColumnType.SMALLINT, false, "0")
                    .column("house_2c", ColumnType.SMALLINT, false, "0")
                    .column("house_3a", ColumnType.SMALLINT, false, "0")
                    .column("house_3b", ColumnType.SMALLINT, false, "0")
                    .column("house_3c", ColumnType.SMALLINT, false, "0")
                    .column("house_3d", ColumnType.SMALLINT, false, "0")
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("gift_acquired")
                    .column("uid", ColumnType.STRING, false)
                    .column("code_id", ColumnType.BIGINT, false, "0")
                    .uniqueIndex("uid", "code_id")
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("gift_code")
                    .column("code", ColumnType.STRING, false)
                    .column("description", ColumnType.TEXT, true)
                    .column("expire_at", ColumnType.TIMESTAMP, true)
                    .uniqueIndex("code")
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("hash_tag")
                    .column("name", ColumnType.STRING, false)
                    .column("claim", ColumnType.SMALLINT, false, "0")
                    .uniqueIndex("name")
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("log")
                    .column("uid", ColumnType.STRING, false)
                    .column("table", ColumnType.STRING, false)
                    .column("action", ColumnType.SMALLINT, true)
                    .column("to_string", ColumnType.STRING, false)
                    .index("uid", "table", "action")
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("stage_gear_drop")
                    .column("stage", ColumnType.SMALLINT, false)
                    .column("map", ColumnType.SMALLINT, false)
                    .column("init_tier", ColumnType.FLOAT, false)
                    .column("lowGrade", ColumnType.SMALLINT, false)
                    .column("highGrade", ColumnType.SMALLINT, true)
                    .uniqueIndex("stage", "map")
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));

            tb = TableSchema.builder("account")
                    .column("uid", ColumnType.STRING, false)
                    .column("ops", ColumnType.SMALLINT, false, "-1")
                    .column("leak", ColumnType.BOOLEAN, false, "false")
                    .uniqueIndex("uid")
                    .build();
            ctx.log("info", "Creating table '%s': %s".formatted(tb.getTableName(), db.createTable(tb)));
        });
    }

    private void createRepositories(PluginDatabaseManager db){
        apostles = db.getRepository("apostle", Apostle.class);
        apostleTrackers = db.getRepository("apostle_track", ApostleTrack.class);
        crayonLineups = db.getRepository("crayon_line_up", CrayonLineUp.class);
        giftAcquires = db.getRepository("gift_acquired", GiftAcquired.class);
        giftCodes = db.getRepository("gift_code", GiftCode.class);
        hashTags = db.getRepository("hash_tag", Hashtag.class);
        logs = db.getRepository("log", Log.class);
        stageGears = db.getRepository("stage_gear_drop", StageGearDrop.class);
        accounts = db.getRepository("account", Account.class);
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