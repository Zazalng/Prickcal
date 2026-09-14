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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getMarkId() {
        return markId;
    }

    public void setMarkId(Long markId) {
        this.markId = markId;
    }

    public Boolean getVoide() {
        return voide;
    }

    public void setVoide(Boolean voide) {
        this.voide = voide;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
