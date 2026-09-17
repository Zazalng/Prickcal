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
import io.github.zazalng.prickcal.global.entities.Account;
import io.github.zazalng.prickcal.global.entities.Apostle;
import io.github.zazalng.prickcal.global.entities.ApostleTrack;
import io.github.zazalng.prickcal.global.entities.CrayonLineUp;
import io.github.zazalng.prickcal.global.exception.PrickcalEnum;
import io.github.zazalng.prickcal.global.exception.PrickcalException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates all Apostle-related business logic:
 * lookup, tracking, crayon operations, deep search, and switch.
 */
public class ApostleManager extends AbstractManager {
    private final PluginRepository<Apostle> repoApostle;
    private final PluginRepository<ApostleTrack> repoTracker;

    protected ApostleManager(ManagerFactory factory) {
        super(factory);
        repoApostle = factory.getRepos().apostles();
        repoTracker = factory.getRepos().apostleTrackers();
    }

    @Override
    public String getTableName() {
        return "apostles";
    }

    @Override
    public ApostleManager initialize() {
        return this;
    }

    @Override
    public void reload() {

    }

    @Override
    public void shutdown() {

    }

    // ==================== LOOKUPS ====================

    /** Find an apostle by its primary key. */
    public Apostle findById(long id) {
        return repoApostle.query()
                .where("id", id)
                .findOne()
                .orElseThrow(() -> new PrickcalException(PrickcalEnum.INVALID_APOSTLE, String.valueOf(id)));
    }

    public boolean isValid(Apostle apostle) {
        if (ApostleColor.fromNo(apostle.getColor()) != ApostleColor.UNKNOWN) return false;
        if (ApostlePosition.fromNo(apostle.getPosition()) != ApostlePosition.UNKNOWN) return false;
        if (ApostleRace.fromNo(apostle.getRace()) != ApostleRace.UNKNOWN) return false;

        return true;
    }

    public boolean isValid(long id) {
        return isValid(findById(id));
    }

    /** List all apostles. */
    public List<Apostle> listAll() {
        return repoApostle.query().list();
    }

    public CrayonLineUp findLineUp(ApostleTrack apostleTrack) {
        return findLineUp(findById(apostleTrack.getApostleId()));
    }

    /** Resolve the crayon lineup for an apostle. */
    public CrayonLineUp findLineUp(Apostle apostle) {
        if (apostle.getCrayon() == null)
            throw new PrickcalException(PrickcalEnum.INVALID_APOSTLE, String.valueOf(apostle.getId()));
        return findLineUp(apostle.getCrayon());
    }

    public CrayonLineUp findLineUp(long id) {
        return repos.crayonLineups().query()
                .where("id", id)
                .findOne()
                .orElseThrow(() -> new PrickcalException(PrickcalEnum.INVALID_CRAYONLINEUP, String.valueOf(id)));
    }

    /**
     * Convert a List of Boolean to the persisted comma-separated string.
     */
    public static String crayonStateToString(List<Boolean> state) {
        return state.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /** Find the tracking record for a user + apostle combination. */
    public ApostleTrack findTrack(long account, long apostleId) {
        return repoTracker.query()
                .where("apostle_id", apostleId)
                .where("uid", account)
                .findOne()
                .orElseGet(() -> createTracker(account, apostleId));
    }

    public ApostleTrack findTrack(Account account, long apostle) {
        return findTrack(account.getId(), apostle);
    }

    public ApostleTrack findTrack(Account account, Apostle apostle) {
        return findTrack(account, apostle.getId());
    }

    private ApostleTrack createTracker(Long uid, long apostleId) {
        ApostleTrack tracker = new ApostleTrack();
        tracker.setUid(uid);
        tracker.setApostleId(apostleId);
        tracker.setCurrentStar((short) 0);
        tracker = repoTracker.save(tracker);
        return tracker;
    }

    public List<ApostleTrack> findTracks(Account account) {
        return findTracks(account.getId());
    }

    public List<ApostleTrack> findTracks(Long id) {
        return repoTracker.findBy("uid", id);
    }

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

    /** Convert persisted comma-separated string back to boolean[]. */
    public static boolean[] stringToCrayonState(String raw) {
        if (raw == null || raw.isEmpty()) return new boolean[0];
        String[] parts = raw.split(",");
        boolean[] state = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++) {
            state[i] = Boolean.parseBoolean(parts[i]);
        }
        return state;
    }

    /** Convert a List<Boolean> to boolean[]. */
    public boolean[] listToState(List<Boolean> list) {
        boolean[] state = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            state[i] = list.get(i);
        }
        return state;
    }

    // ==================== CONFIRM / PERSIST ====================

    /**
     * Persist a crayon toggle state for a user + apostle.
     * Creates a new track if none exists; updates otherwise.
     * Logs the operation.
     */
    public ApostleTrack confirmCrayon(Account account, Apostle apostle, List<Boolean> state) {
        String crayonStr = crayonStateToString(state);

        ApostleTrack track = findTrack(account, apostle.getId());
        if (track == null) {
            track = new ApostleTrack();
            track.setApostleId(apostle.getId());
            track.setUid(account.getId());
            track.setCurrentStar(apostle.getInit());
            track.setCrayon(crayonStr);
            factory.getRepos().apostleTrackers().save(track);
            logCreated(account.getId(),
                    "Created crayon track for " + apostle.getName());
        } else {
            track.setCrayon(crayonStr);
            factory.getRepos().apostleTrackers().save(track);
            logUpdated(account.getId(), "Updated crayon track for " + apostle.getName());
        }
        return track;
    }

    // ==================== DEEP SEARCH ====================

    /**
     * Apply chained filters: name, then race, then color, then position. Stops early when 1 result.
     */
    public void deepFilter(Account account) {
        List<String> config = sessionManager().getApostleSearch(account.getId());
        if (config == null || config.size() < 7) {
            return;
        }

        String name = config.get(0);
        List<Short> raceFilters = parseShortList(config.get(3));
        List<Short> colorFilters = parseShortList(config.get(4));
        List<Short> positionFilters = parseShortList(config.get(5));

        QueryBuilder<Apostle> query = repoApostle.query();

        if (name != null && !name.isBlank()) {
            query.whereILike("name", "%" + name.trim() + "%");
        }

        List<Apostle> results = query.list();

        if (results.size() > 1 && !raceFilters.isEmpty()) {
            query.whereIn("race", raceFilters);
            results = query.list();
        }

        if (results.size() > 1 && !colorFilters.isEmpty()) {
            query.whereIn("color", colorFilters);
            results = query.list();
        }

        if (results.size() > 1 && !positionFilters.isEmpty()) {
            query.whereIn("position", positionFilters);
            results = query.list();
        }

        config.set(6, results.stream()
                .map(a -> String.valueOf(a.getId()))
                .collect(Collectors.joining(",")));
        sessionManager().setApostleSearch(account.getId(), config);
    }

    // ==================== HELPERS ====================
    private List<Integer> parseIntegerList(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .toList();
    }

    private List<Short> parseShortList(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Short::valueOf)
                .toList();
    }
}
