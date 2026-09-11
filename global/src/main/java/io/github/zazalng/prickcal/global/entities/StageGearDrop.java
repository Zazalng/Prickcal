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

@Entity(tableName = "stage_gear_drops")
public class StageGearDrop {
    /**
     * Key record column require by API
     */
    @Column
    private Long id;
    /**
     * Which stage number
     */
    @Column(unique = true)
    private int stage;
    /**
     * Which map in stage number
     */
    @Column(unique = true)
    private int map;
    /**
     * Which Tier number of this stage hold (can be only \d+\.[0,5] as valid value)
     */
    @Column
    private float initTier;
    /**
     * Which gear type id from {@link io.github.zazalng.prickcal.global.contract.trickcal.GearType}
     */
    @Column
    private int lowGrade;
    /**
     * Which gear type id from {@link io.github.zazalng.prickcal.global.contract.trickcal.GearType}
     */
    @Column
    private int highGrade;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public StageGearDrop() {
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    public float getInitTier() {
        return initTier;
    }

    public void setInitTier(float initTier) {
        this.initTier = initTier;
    }

    public int getLowGrade() {
        return lowGrade;
    }

    public void setLowGrade(int lowGrade) {
        this.lowGrade = lowGrade;
    }

    public int getHighGrade() {
        return highGrade;
    }

    public void setHighGrade(int highGrade) {
        this.highGrade = highGrade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
