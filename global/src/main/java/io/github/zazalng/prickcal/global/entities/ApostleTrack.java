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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
     * Belongs to {@link Account}.uid
     */
    @Column(unique = true, nullable = false)
    private String uid;
    /**
     * Character of {@code Apostle.id} from {@code Account.uid}'s star level (cannot below {@code Apostle.init} or above {@code Apostle.max})
     */
    @Column
    private int currentStar;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCurrentStar() {
        return currentStar;
    }

    public void setCurrentStar(int star) {
        this.currentStar = star;
    }

    public String getCrayon() {
        return crayon;
    }

    public void setCrayon(String crayon) {
        this.crayon = crayon;
    }

    public List<Boolean> getCrayons() {
        List<Boolean> crayons = new ArrayList<>();
        for (String c : getCrayon().split(",")) {
            crayons.addLast(Boolean.parseBoolean(c.toLowerCase()));
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
}
