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
import java.time.LocalDate;

@Entity(tableName = "crayon_records")
public class CrayonRecord {
    @Column
    private Long id;
    @Column(nullable = false, index = true)
    private Long uid;
    @Column
    private String imgUrl;
    @Column
    private int spent;
    @Column
    private int crayon;
    @Column
    private LocalDate recordDate;
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

    /** Belong to {@link Account}.id */
    public Long getUid() {
        return uid;
    }

    /** Belong to {@link Account}.id */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /** An image url of this crayon record (from discord.attachment) */
    public String getImgUrl() {
        return imgUrl;
    }

    /** An image url of this crayon record (from discord.attachment) */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /** A candy spent on this crayon record */
    public int getSpent() {
        return spent;
    }

    /** A candy spent on this crayon record */
    public void setSpent(int spent) {
        this.spent = spent;
    }

    /** A crayon acquired on this crayon record */
    public int getCrayon() {
        return crayon;
    }

    /** A crayon acquired on this crayon record */
    public void setCrayon(int crayon) {
        this.crayon = crayon;
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

    /** A date of this crayon record that parsed from the user crayon format */
    public LocalDate getRecordDate() {
        return recordDate;
    }

    /** A date of this crayon record that parsed from the user crayon format */
    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }
}
