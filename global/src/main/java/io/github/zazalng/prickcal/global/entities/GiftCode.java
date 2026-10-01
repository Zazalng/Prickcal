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

@Entity(tableName = "gift_codes")
public class GiftCode {
    /**
     * Key record column require by API (as well as using for tracking from {@link GiftAcquired}.codeId)
     */
    @Column
    private Long id;
    /**
     * A code of gift
     */
    @Column(nullable = false, unique = true)
    private String code;
    /**
     * A describe of gift reward
     */
    @Column
    private String description;
    /**
     * The image URL associated with reward of this gift code.
     */
    @Column
    private String imgUrl;
    /**
     * An expiry timestamp of gift code
     */
    @Column
    private Instant expireAt;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    /**
     * Key record column require by API (as well as using for tracking from {@link GiftAcquired}.codeId)
     */
    public Long getId() {
        return id;
    }

    /**
     * Key record column require by API (as well as using for tracking from {@link GiftAcquired}.codeId)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** A code of gift */
    public String getCode() {
        return code;
    }

    /** A code of gift */
    public void setCode(String code) {
        this.code = code;
    }

    /** A describe of gift reward */
    public String getDescription() {
        return description;
    }

    /** A describe of gift reward */
    public void setDescription(String description) {
        this.description = description;
    }

    /** An expiry timestamp of gift code */
    public Instant getExpireAt() {
        return expireAt;
    }

    /** An expiry timestamp of gift code */
    public void setExpireAt(Instant expireAt) {
        this.expireAt = expireAt;
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

    /** The image URL associated with reward of this gift code. */
    public String getImgUrl() {
        return imgUrl;
    }

    /** The image URL associated with reward of this gift code. */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
