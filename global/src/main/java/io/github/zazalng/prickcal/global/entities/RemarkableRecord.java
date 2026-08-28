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

@Entity
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
    private String uid;
    /**
     * Which table of database get invoice
     */
    @Column(unique = true)
    private String table;
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
     * Given reason to void this remarkable by {@link Operator.ADMIN}
     */
    @Column
    private String reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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
}
