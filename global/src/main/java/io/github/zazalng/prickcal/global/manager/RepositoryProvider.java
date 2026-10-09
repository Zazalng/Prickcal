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

    /**
     * Repository of {@link Account} rows.
     */
    PluginRepository<Account> accounts();

    /**
     * Repository of {@link Apostle} rows.
     */
    PluginRepository<Apostle> apostles();

    /** Repository of {@link ApostleRemarkable} rows. */
    PluginRepository<ApostleRemarkable> apostleRemarkables();

    /** Repository of {@link ApostleTrack} rows. */
    PluginRepository<ApostleTrack> apostleTrackers();

    /** Repository of {@link CrayonLineUp} rows. */
    PluginRepository<CrayonLineUp> crayonLineups();

    /** Repository of {@link CrayonRecord} rows. */
    PluginRepository<CrayonRecord> crayonRecords();

    /** Repository of {@link GiftAcquired} rows. */
    PluginRepository<GiftAcquired> giftAcquires();

    /** Repository of {@link GiftCode} rows. */
    PluginRepository<GiftCode> giftCodes();

    /** Repository of {@link Hashtag} rows. */
    PluginRepository<Hashtag> hashTags();

    /** Repository of {@link Log} rows. */
    PluginRepository<Log> logs();

    /** Repository of {@link RemarkableRecord} rows. */
    PluginRepository<RemarkableRecord> remarkableRecords();

    /** Repository of {@link StageGearDrop} rows. */
    PluginRepository<StageGearDrop> stageGears();

    /**
     * Repository of {@link TemplateUser} rows.
     */
    PluginRepository<TemplateUser> templateUsers();

    /**
     * Resolve a Discord user, trying the cache first and the REST retrieve second.
     *
     * @param uid the Discord user id to resolve
     * @return the resolved Discord user
     */
    User getDiscordUser(String uid);
}
