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

@Entity(tableName = "remarkable_records")
public class RemarkableRecord {
    /**
     * Key record column require by API
     */
    @Column
    private Long id;
    /**
     * Belong to {@link Account}.uid
     */
    @Column(unique = true)
    private Long uid;
    /**
     * Which table of database get invoice
     */
    @Column(unique = true)
    private String tableName;
    /**
     * Which id of {@code table} from database get invoice
     */
    @Column(unique = true)
    private Long markId;
    /**
     * Is this remarkable get void?
     */
    @Column
    private Boolean voide;
    /**
     * Given reason to void this remarkable by {@link io.github.zazalng.prickcal.global.contract.operator.Operator#ADMIN}
     */
    @Column
    private String reason;

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

    /** Belong to {@link Account}.uid */
    public Long getUid() {
        return uid;
    }

    /** Belong to {@link Account}.uid */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /** Which table of database get invoice */
    public String getTableName() {
        return tableName;
    }

    /** Which table of database get invoice */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /** Which id of {@code table} from database get invoice */
    public Long getMarkId() {
        return markId;
    }

    /** Which id of {@code table} from database get invoice */
    public void setMarkId(Long markId) {
        this.markId = markId;
    }

    /** Is this remarkable get void? */
    public Boolean getVoide() {
        return voide;
    }

    /** Is this remarkable get void? */
    public void setVoide(Boolean voide) {
        this.voide = voide;
    }

    /** Given reason to void this remarkable by {@link io.github.zazalng.prickcal.global.contract.operator.Operator}#ADMIN */
    public String getReason() {
        return reason;
    }

    /** Given reason to void this remarkable by {@link io.github.zazalng.prickcal.global.contract.operator.Operator}#ADMIN */
    public void setReason(String reason) {
        this.reason = reason;
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
