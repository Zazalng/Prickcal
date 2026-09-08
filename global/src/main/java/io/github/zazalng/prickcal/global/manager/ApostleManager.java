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

import io.github.zazalng.prickcal.global.contract.operator.Action;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats;
import io.github.zazalng.prickcal.global.entities.*;
import io.github.zazalng.prickcal.global.exception.PrickcalEnum;
import io.github.zazalng.prickcal.global.exception.PrickcalException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Encapsulates all Apostle-related business logic:
 * lookup, tracking, crayon operations, deep search, and switch.
 * Every state change is logged through {@link LogManager}.
 */
public class ApostleManager {

    private final RepositoryProvider repos;
    private final LogManager logManager;

    public ApostleManager(RepositoryProvider repos, LogManager logManager) {
        this.repos = repos;
        this.logManager = logManager;
    }

    // ==================== LOOKUPS ====================

    /** Find an apostle by its primary key. */
    public Optional<Apostle> findById(long id) {
        return repos.apostles().query()
                .where("id", id)
                .findOne();
    }

    /** List all apostles. */
    public List<Apostle> listAll() {
        return repos.apostles().query().list();
    }

    /** Resolve the crayon lineup for an apostle. */
    public Optional<CrayonLineUp> findLineUp(Apostle apostle) {
        if (apostle.getCrayon() == null) return Optional.empty();
        return repos.crayonLineups().query()
                .where("id", apostle.getCrayon())
                .findOne();
    }

    /** Resolve the crayon lineup, throwing if missing. */
    public CrayonLineUp requireLineUp(Apostle apostle) {
        return findLineUp(apostle).orElseThrow(() ->
                new PrickcalException(PrickcalEnum.INVALID_CRAYONLINEUP,
                        String.valueOf(apostle.getCrayon())));
    }

    /** Find the tracking record for a user + apostle combination. */
    public Optional<ApostleTrack> findTrack(String uid, long apostleId) {
        return repos.apostleTrackers().query()
                .where("uid", uid)
                .where("apostle_id", apostleId)
                .findOne();
    }

    // ==================== CRAYON TOGGLE ====================

    /** Convert a boolean[] to the persisted comma-separated string. */
    public static String crayonStateToString(boolean[] state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < state.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(state[i]);
        }
        return sb.toString();
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
    public static boolean[] listToState(List<Boolean> list) {
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
    public ApostleTrack confirmCrayon(String uid, Apostle apostle, boolean[] state) {
        String crayonStr = crayonStateToString(state);

        ApostleTrack track = findTrack(uid, apostle.getId()).orElse(null);
        if (track == null) {
            track = new ApostleTrack();
            track.setApostleId(apostle.getId());
            track.setUid(uid);
            track.setCurrentStar(apostle.getInit());
            track.setCrayon(crayonStr);
            repos.apostleTrackers().save(track);
            logManager.created(uid, "apostle_tracks",
                    "Created crayon track for " + apostle.getName());
        } else {
            track.setCrayon(crayonStr);
            repos.apostleTrackers().save(track);
            logManager.updated(uid, "apostle_tracks",
                    "Updated crayon track for " + apostle.getName());
        }
        return track;
    }

    // ==================== DEEP SEARCH ====================

    /** Filter apostles by name (case-insensitive contains). */
    public List<Apostle> searchByName(List<Apostle> source, String name) {
        if (name == null || name.isEmpty()) return source;
        String lower = name.trim().toLowerCase();
        return source.stream()
                .filter(a -> a.getName() != null && a.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /** Filter apostles by color/personality. */
    public List<Apostle> filterByColor(List<Apostle> source, int colorNo) {
        return source.stream()
                .filter(a -> a.getColor() == colorNo)
                .collect(Collectors.toList());
    }

    /** Filter apostles by position/race. */
    public List<Apostle> filterByRace(List<Apostle> source, int raceNo) {
        return source.stream()
                .filter(a -> a.getRace() == raceNo)
                .collect(Collectors.toList());
    }

    /** Apply chained filters: name, then color, then race. Stops early when 1 result. */
    public List<Apostle> deepFilter(List<Apostle> source, String name,
                                    List<Integer> colorFilters, List<Integer> positionFilters) {
        List<Apostle> results = source;

        if (name != null && !name.isEmpty()) {
            results = searchByName(results, name);
            if (results.size() <= 1) return results;
        }

        if (colorFilters != null && !colorFilters.isEmpty()) {
            List<Apostle> filtered = results.stream()
                    .filter(a -> colorFilters.contains(a.getColor()))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                results = filtered;
                if (results.size() <= 1) return results;
            }
        }

        if (positionFilters != null && !positionFilters.isEmpty()) {
            List<Apostle> filtered = results.stream()
                    .filter(a -> positionFilters.contains(a.getRace()))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                results = filtered;
            }
        }

        return results;
    }

    // ==================== HELPERS ====================

    public static List<CrayonStats> defaultCrayonStats() {
        return Arrays.asList(
                CrayonStats.ATK, CrayonStats.HP, CrayonStats.CRIT,
                CrayonStats.DEF, CrayonStats.CRES
        );
    }
}
