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
package io.github.zazalng.prickcal.global.manager;

import group.worldstandard.pudel.api.database.PluginRepository;
import io.github.zazalng.prickcal.global.entities.*;
import net.dv8tion.jda.api.entities.User;

/**
 * Standalone provider of all repositories and external lookups.
 * Extracted from the old inner-interface on PrickcalButtonHandler so that
 * every manager and handler can reference it without coupling to a specific handler class.
 */
public interface RepositoryProvider {

    PluginRepository<Account> accounts();

    PluginRepository<Apostle> apostles();

    PluginRepository<ApostleRemarkable> apostleRemarkables();

    PluginRepository<ApostleTrack> apostleTrackers();

    PluginRepository<CrayonLineUp> crayonLineups();

    PluginRepository<CrayonRecord> crayonRecords();

    PluginRepository<GiftAcquired> giftAcquires();

    PluginRepository<GiftCode> giftCodes();

    PluginRepository<Hashtag> hashTags();

    PluginRepository<Log> logs();

    PluginRepository<RemarkableRecord> remarkableRecords();

    PluginRepository<StageGearDrop> stageGears();

    /** Resolve a Discord user — tries cache first, then REST retrieve. */
    User getDiscordUser(String uid);
}
