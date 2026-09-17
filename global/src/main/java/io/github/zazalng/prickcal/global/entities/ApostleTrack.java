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
     * Character of {@code Apostle.id} from {@code Account.uid}'s star level (cannot below {@code Apostle.init} or above {@code Apostle.max})
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApostleId() {
        return apostleId;
    }

    public void setApostleId(Long apostleId) {
        this.apostleId = apostleId;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public short getCurrentStar() {
        return currentStar;
    }

    public void setCurrentStar(short star) {
        this.currentStar = star;
    }

    public String getCrayon() {
        return crayon;
    }

    public void setCrayon(String crayon) {
        this.crayon = crayon;
    }

    public ApostleTrack updateCrayon(List<Boolean> crayons) {
        if (crayons != null) {
            this.crayon = crayons.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
        return this;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isOwned() {
        return getCurrentStar() != 0;
    }

    public int totalSpent(List<Integer> depth) {
        int totalSpent = 0;
        for (int i = 0; i < getCrayons().size(); i++) {
            if (getCrayons().get(i)) totalSpent += CrayonCosts.fromDepth(depth.get(i)).getPrice();
        }
        return totalSpent;
    }
}
