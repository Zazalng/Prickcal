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
public class ApostleTrack {
    @Column
    private Long id;

    @Column
    private Long apostleId;

    @Column
    private String uid;

    @Column
    private int currentStar;

    @Column
    private String crayon;

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

    /**
     * Custom Method for Logic
     */


}
