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

@Entity(tableName = "logs")
public class Log {
    /**
     * Key record column require by API
     */
    @Column
    private Long id;
    /**
     * Belong to {@link Account}.uid
     */
    @Column(index = true, nullable = false)
    private Long uid;
    /**
     * Which table of database get invoice
     */
    @Column(index = true, nullable = false)
    private String tableName;
    /**
     * Which action this log do (match {@link io.github.zazalng.prickcal.global.contract.operator.Action})
     */
    @Column(index = true, nullable = false)
    private String action;
    /**
     * What it does in plaintext
     */
    @Column(nullable = false)
    private String toString;

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

    /** Which action this log do (match {@link io.github.zazalng.prickcal.global.contract.operator.Action}) */
    public String getAction() {
        return action;
    }

    /** Which action this log do (match {@link io.github.zazalng.prickcal.global.contract.operator.Action}) */
    public void setAction(String action) {
        this.action = action;
    }

    /** What it does in plaintext */
    public String getToString() {
        return toString;
    }

    /** What it does in plaintext */
    public void setToString(String toString) {
        this.toString = toString;
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
