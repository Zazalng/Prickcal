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

@Entity(tableName = "apostle_reviews")
public class ApostleRemarkable {
    /**
     * Key record column require by API (as well using for tracking from {@link RemarkableRecord})
     */
    @Column
    private Long id;
    /**
     * Belong to {@link Apostle}.id
     */
    @Column(unique = true, nullable = false)
    private Long apostleId;
    /**
     * Belong to {@link Account}.id
     */
    @Column(unique = true, nullable = false)
    private Long uid;
    /**
     * A message that uid post about this character (by {@code ApostleRemarkable.apostleId})
     */
    @Column(nullable = false)
    private String msg;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    /**
     * Key record column require by API (as well using for tracking from {@link RemarkableRecord})
     */
    public Long getId() {
        return id;
    }

    /**
     * Key record column require by API (as well using for tracking from {@link RemarkableRecord})
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** Belong to {@link Apostle}.id */
    public Long getApostleId() {
        return apostleId;
    }

    /** Belong to {@link Apostle}.id */
    public void setApostleId(Long apostleId) {
        this.apostleId = apostleId;
    }

    /**
     * Belong to {@link Account}.id
     */
    public Long getUid() {
        return uid;
    }

    /** Belong to {@link Account}.id */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /** A message that uid post about this character (by {@code ApostleRemarkable.apostleId}) */
    public String getMsg() {
        return msg;
    }

    /** A message that uid post about this character (by {@code ApostleRemarkable.apostleId}) */
    public void setMsg(String msg) {
        this.msg = msg;
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
}
