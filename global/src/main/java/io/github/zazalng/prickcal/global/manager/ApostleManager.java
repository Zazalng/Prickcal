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
import group.worldstandard.pudel.api.database.QueryBuilder;
import io.github.zazalng.prickcal.global.contract.trickcal.HashtagClaim;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleColor;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostlePosition;
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleRace;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonCosts;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats;
import io.github.zazalng.prickcal.global.dto.ApostleSearch;
import io.github.zazalng.prickcal.global.entities.Account;
import io.github.zazalng.prickcal.global.entities.Apostle;
import io.github.zazalng.prickcal.global.entities.ApostleTrack;
import io.github.zazalng.prickcal.global.entities.CrayonLineUp;
import io.github.zazalng.prickcal.global.exception.PrickcalEnum;
import io.github.zazalng.prickcal.global.exception.PrickcalException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Owns {@link Apostle} and {@link ApostleTrack} rows, the crayon line-up lookups
 * derived from them, and the hashtag summary rendering of an apostle.
 */
public class ApostleManager extends AbstractManager {
    private final PluginRepository<Apostle> repoApostle;
    private final PluginRepository<ApostleTrack> repoTracker;

    /**
     * Binds the apostle and apostle track repositories.
     *
     * @param factory the owning factory
     */
    protected ApostleManager(ManagerFactory factory) {
        super(factory);
        repoApostle = factory.getRepos().apostles();
        repoTracker = factory.getRepos().apostleTrackers();
    }

    /**
     * Convert a List of Boolean to the persisted comma-separated string.
     *
     * @param state the toggle flags in slot order
     * @return the flags joined by commas, for example {@code "true,false,true"}
     */
    public static String crayonStateToString(List<Boolean> state) {
        return state.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * Table name written into the audit log entries of this manager.
     *
     * @return {@code "apostles"}
     */
    @Override
    public String getTableName() {
        return "apostles";
    }

    /**
     * No setup is required; the manager is ready once constructed.
     *
     * @return this manager
     */
    @Override
    public ApostleManager initialize() {
        return this;
    }

    /**
     * Intentionally empty: this manager keeps no cache to reload.
     */
    @Override
    public void reload() {

    }

    /**
     * Intentionally empty: this manager holds no state to release on shutdown.
     */
    @Override
    public void shutdown() {

    }

    // ==================== LOOKUPS ====================

    /**
     * Find an apostle by its primary key.
     *
     * @param id the apostle primary key
     * @return the matching apostle, never null
     * @throws PrickcalException if no apostle carries that id
     */
    public Apostle findById(long id) {
        return repoApostle.query()
                .where("id", id)
                .findOne()
                .orElseThrow(() -> new PrickcalException(PrickcalEnum.INVALID_APOSTLE, String.valueOf(id)));
    }

    /**
     * Check the color, position and race stored on an apostle.
     *
     * @param apostle the apostle to inspect
     * @return {@code true} when color, position and race all not resolve to UNKNOWN
     */
    public boolean isValid(Apostle apostle) {
        if (ApostleColor.fromNo(apostle.getColor()) == ApostleColor.UNKNOWN) return false;
        if (ApostlePosition.fromNo(apostle.getPosition()) == ApostlePosition.UNKNOWN) return false;
        if (ApostleRace.fromNo(apostle.getRace()) == ApostleRace.UNKNOWN) return false;

        return true;
    }

    /**
     * Check an apostle resolved by its id.
     *
     * @param id the apostle primary key
     * @return the verdict of {@link #isValid(Apostle)} for that apostle
     * @throws PrickcalException if no apostle carries that id
     */
    public boolean isValid(long id) {
        return isValid(findById(id));
    }

    /**
     * List all apostles.
     *
     * @return every apostle row, ordered by name ascending
     */
    public List<Apostle> listAll() {
        return repoApostle.query().orderByAsc("name").list();
    }

    /**
     * Apply chained filters: name, then race, then color, then position. Stops early when 1 result.
     * Every stage after the name lookup is skipped while the previous stage still matched more
     * than one apostle, and the filters accumulate on a single query.
     *
     * @param config the search configuration holding the name guess and the race, color and position filters
     * @return the apostles left after the chained filters, name-sorted only when the name stage ran
     */
    public List<Apostle> deepFilter(ApostleSearch config) {
        String name = config.getSfGuessName();

        QueryBuilder<Apostle> query = repoApostle.query();

        if (name != null && !name.isBlank()) {
            query.whereILike("name", "%" + name.trim() + "%");
        }

        List<Apostle> results = query.list();

        if (results.size() > 1 && !config.getSfRaceFilter().isEmpty()) {
            query.whereIn("race", config.getSfRaceFilter());
            results = query.list();
        }

        if (results.size() > 1 && !config.getSfColorFilter().isEmpty()) {
            query.whereIn("color", config.getSfColorFilter());
            results = query.list();
        }

        if (results.size() > 1 && !config.getSfPositionFilter().isEmpty()) {
            query.whereIn("position", config.getSfPositionFilter());
            results = query.list();
        }

        return results;
    }

    /**
     * Resolve the crayon line-up of the apostle a track points at.
     *
     * @param apostleTrack the track whose apostle id is followed
     * @return the line-up of the referenced apostle
     * @throws PrickcalException if the track is unresolvable or its apostle carries no line-up id
     */
    public CrayonLineUp findLineUp(ApostleTrack apostleTrack) {
        return findLineUp(findById(apostleTrack.getApostleId()));
    }

    /**
     * Resolve the crayon lineup for an apostle.
     *
     * @param apostle the apostle whose crayon id is followed
     * @return the matching crayon line-up row
     * @throws PrickcalException if the apostle carries no crayon id
     */
    public CrayonLineUp findLineUp(Apostle apostle) {
        if (apostle.getCrayon() == null)
            throw new PrickcalException(PrickcalEnum.INVALID_APOSTLE, String.valueOf(apostle.getId()));
        return findLineUp(apostle.getCrayon());
    }

    /**
     * Find a crayon line-up by its primary key.
     *
     * @param id the crayon line-up primary key
     * @return the matching line-up, never null
     * @throws PrickcalException if no line-up carries that id
     */
    public CrayonLineUp findLineUp(long id) {
        return repos.crayonLineups().query()
                .where("id", id)
                .findOne()
                .orElseThrow(() -> new PrickcalException(PrickcalEnum.INVALID_CRAYONLINEUP, String.valueOf(id)));
    }

    /**
     * Find the tracking record for a user + apostle combination.
     *
     * @param account the owning account id
     * @param apostleId the apostle primary key
     * @return the existing track, or a newly created one starting at zero stars
     */
    public ApostleTrack findTrack(long account, long apostleId) {
        return repoTracker.query()
                .where("apostle_id", apostleId)
                .where("uid", account)
                .findOne()
                .orElseGet(() -> createTracker(account, apostleId));
    }

    /**
     * Find the tracking record for an account + apostle combination.
     *
     * @param account the owning account
     * @param apostle the apostle primary key
     * @return the existing track, or a newly created one starting at zero stars
     */
    public ApostleTrack findTrack(Account account, long apostle) {
        return findTrack(account.getId(), apostle);
    }

    /**
     * Find the tracking record for an account + apostle combination.
     *
     * @param account the owning account
     * @param apostle the tracked apostle
     * @return the existing track, or a newly created one starting at zero stars
     */
    public ApostleTrack findTrack(Account account, Apostle apostle) {
        return findTrack(account, apostle.getId());
    }

    /**
     * Create and persist a zero-star track for an apostle.
     *
     * @param uid the owning account id
     * @param apostleId the apostle primary key
     * @return the persisted track
     */
    private ApostleTrack createTracker(Long uid, long apostleId) {
        ApostleTrack tracker = new ApostleTrack();
        tracker.setUid(uid);
        tracker.setApostleId(apostleId);
        tracker.setCurrentStar((short) 0);
        tracker = repoTracker.save(tracker);
        return tracker;
    }

    /**
     * List every apostle tracked by an account.
     *
     * @param account the owning account
     * @return the tracks of that account, empty when the user owns no apostle
     */
    public List<ApostleTrack> findTracks(Account account) {
        return findTracks(account.getId());
    }

    /**
     * List every apostle tracked by an account id.
     *
     * @param id the owning account id
     * @return the tracks of that account, empty when the user owns no apostle
     */
    public List<ApostleTrack> findTracks(Long id) {
        return repoTracker.findBy("uid", id);
    }

    /**
     * Render the hashtag claims of an apostle as a Markdown summary.
     * Claim ids that are blank, unknown or unreadable are skipped silently.
     *
     * @param apostle the apostle whose comma separated hashtag ids are resolved
     * @return a Markdown block with a Pros, a Nature and a Cons section, or {@code "*none*"}
     *         when the apostle carries no hashtag
     */
    public String parseHashTag(Apostle apostle) {
        if (apostle == null || apostle.getHashtag() == null || apostle.getHashtag().isBlank()) {
            return "*none*";
        }

        StringBuilder pros = new StringBuilder("### Pros\n");
        StringBuilder nature = new StringBuilder("### Nature\n");
        StringBuilder cons = new StringBuilder("### Cons\n");

        for (String rawId : apostle.getHashtag().split(",")) {
            String hid = rawId.trim();

            if (hid.isEmpty()) {
                continue;
            }

            repos.hashTags().findById(Long.parseLong(hid)).ifPresent(tag -> {
                HashtagClaim claim;

                try {
                    claim = HashtagClaim.fromValue(tag.getClaim());
                } catch (Exception e) {
                    return;
                }

                String hashtag = "`%s`".formatted(tag.getName());

                switch (claim) {
                    case POSITIVE -> pros.append(hashtag).append('\n');
                    case NEGATIVE -> cons.append(hashtag).append('\n');
                    default -> nature.append(hashtag).append('\n');
                }
            });
        }

        return "%s\n%s\n%s".formatted(
                pros.toString().trim(),
                nature.toString().trim(),
                cons.toString().trim()
        );
    }

    // ==================== CRAYON TOGGLE ====================

    /**
     * Sum what one crayon stat costs across every apostle, regardless of what is unlocked.
     *
     * @param stats the crayon stat to total
     * @return a {@code "{amount},{price}"} string
     * @throws PrickcalException if an apostle resolves to an invalid crayon line-up
     */
    public String crayonTotalByStats(CrayonStats stats) {
        int totalAmount = 0;
        int totalPrice = 0;

        for (Apostle a : listAll()) {
            CrayonLineUp lineUp = findLineUp(a);
            if (!lineUp.isValid()) {
                throw new PrickcalException(PrickcalEnum.INVALID_CRAYONLINEUP, String.valueOf(lineUp.getId()));
            }

            List<Short> lineUpList = lineUp.getLineUp();
            List<Integer> depthList = lineUp.getDepth();

            if (lineUpList == null || depthList == null) {
                continue;
            }

            int limit = Math.min(lineUpList.size(), depthList.size());

            for (int i = 0; i < limit; i++) {
                Short targetStat = lineUpList.get(i);
                Integer depthValue = depthList.get(i);

                if (targetStat == null || depthValue == null || stats.getNo() != targetStat) {
                    continue;
                }

                CrayonCosts cost = CrayonCosts.fromDepth(depthValue);
                if (cost != null) {
                    totalAmount += cost.getAmount();
                    totalPrice += cost.getPrice();
                }
            }
        }

        return "%d,%d".formatted(totalAmount, totalPrice);
    }

    // ==================== CONFIRM / PERSIST ====================

    /**
     * Persist a crayon toggle state for a user + apostle.
     * The caller owns the track; this method saves it as it stands and returns the
     * persisted row, so an existing track is updated and an unsaved one is inserted.
     *
     * @param track the track carrying the toggled crayon state
     * @return the persisted track
     */
    public ApostleTrack confirmTrackUpdate(ApostleTrack track) {
        return factory.getRepos().apostleTrackers().save(track);
    }
}
