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
package io.github.zazalng.prickcal.global.entities;

import group.worldstandard.pudel.api.database.Column;
import group.worldstandard.pudel.api.database.Entity;
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonCosts;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity(tableName = "apostle_tracks")
public class ApostleTrack {
    /**
     * Key record column require by API
     */
    @Column
    private Long id;
    /**
     * Belongs to {@link Apostle}.id
     */
    @Column(unique = true, nullable = false)
    private Long apostleId;
    /**
     * Belongs to {@link Account}.id
     */
    @Column(unique = true, nullable = false)
    private Long uid;
    /**
     * Character of {@code Apostle.id} from {@code Account.id}'s star level (cannot below {@code Apostle.init} or above {@code Apostle.max})
     */
    @Column
    private short currentStar;
    /**
     * Crayon record for this Character of {@code Apostle.id} from {@code Account.uid}
     * In database this value will record in String but maintain Array convertable by using String.split(",", 9)
     * <p>
     * Example
     * {@code "true,false,true,false,true,false,true,false,true"}
     */
    @Column(nullable = false, defaultValue = "false,false,false,false,false,false,false,false,false")
    private String crayon;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    /**
     * Key record column require by API
     */
    public Long getId() {
        return id;
    }

    /**
     * Key record column require by API
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** Belongs to {@link Apostle}.id */
    public Long getApostleId() {
        return apostleId;
    }

    /** Belongs to {@link Apostle}.id */
    public void setApostleId(Long apostleId) {
        this.apostleId = apostleId;
    }

    /** Belongs to {@link Account}.id */
    public Long getUid() {
        return uid;
    }

    /** Belongs to {@link Account}.id */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /** Character of {@code Apostle.id} from {@code Account.uid}'s star level (cannot below {@code Apostle.init} or above {@code Apostle.max}) */
    public short getCurrentStar() {
        return currentStar;
    }

    /** Character of {@code Apostle.id} from {@code Account.uid}'s star level (cannot below {@code Apostle.init} or above {@code Apostle.max}) */
    public void setCurrentStar(short star) {
        this.currentStar = star;
    }

    /**
     * Moves this track's current star level one step.
     *
     * <p>When increasing, the star level cannot fall below {@code apostle.init}.
     * When decreasing, the star level becomes {@code 0} if it drops below
     * {@code apostle.init}. In either direction, it cannot exceed
     * {@code apostle.max}.</p>
     *
     * @param apostle the character this track belongs to
     * @param increase {@code true} to increase the star level,
     *                 {@code false} to decrease it
     * @return this track
     */
    public ApostleTrack updateCurrentStar(Apostle apostle, boolean increase) {
        int current = getCurrentStar() + (increase ? 1 : -1);

        if (increase) {
            current = Math.max(current, apostle.getInit());
        } else if (current < apostle.getInit()) {
            current = 0;
        }

        setCurrentStar((short) Math.min(current, apostle.getMax()));
        return this;
    }

    /**
     * Crayon record for this Character of {@code Apostle.id} from {@code Account.id}
     */
    public String getCrayon() {
        return crayon;
    }

    /** Crayon record for this Character of {@code Apostle.id} from {@code Account.id} */
    public void setCrayon(String crayon) {
        this.crayon = crayon;
    }

    /**
     * Write the given crayon state back to {@link #getCrayon()} as a comma-separated String.
     *
     * @param crayons the state of every crayon slot, or {@code null} to leave the stored value untouched
     * @return this track
     */
    public ApostleTrack updateCrayon(List<Boolean> crayons) {
        if (crayons != null) {
            this.crayon = crayons.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
        return this;
    }

    /**
     * Read {@link #getCrayon()} back to the state of every crayon slot.
     *
     * @return an empty List when the stored value is null or blank, otherwise one flag per comma-separated token
     */
    public List<Boolean> getCrayons() {
        String raw = getCrayon();
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }

        String[] tokens = raw.split(",");
        List<Boolean> crayons = new ArrayList<>(tokens.length);

        for (String c : tokens) {
            crayons.add(Boolean.parseBoolean(c.trim()));
        }

        return crayons;
    }

    /** Instant of record creation. */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** Instant of record creation. */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /** Instant of last update. */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /** Instant of last update. */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** Whether this track's star level is not 0, i.e. the character is own. */
    public boolean isOwned() {
        return getCurrentStar() != 0;
    }

    /**
     * Sum the crayon price of every crayon slot that is recorded as spent.
     *
     * @param depth the depth of each crayon slot as {@link CrayonLineUp#getDepth()} return
     * @return the total crayon spent to own this character
     */
    public int totalSpent(List<Integer> depth) {
        int totalSpent = 0;
        for (int i = 0; i < getCrayons().size(); i++) {
            if (getCrayons().get(i)) totalSpent += CrayonCosts.fromDepth(depth.get(i)).getPrice();
        }
        return totalSpent;
    }

    /**
     * Render this track's star level as a star emoji for display.
     *
     * @param apostle the character that this track belong to
     * @return {@code "_Not Owning_"} while the star level is still below {@code apostle.init}, otherwise one star emoji per star
     */
    public String printStar(Apostle apostle) {
        if (getCurrentStar() < apostle.getInit()) return "_Not Owning_";
        return "⭐".repeat(Math.max(0, getCurrentStar()));
    }
}
