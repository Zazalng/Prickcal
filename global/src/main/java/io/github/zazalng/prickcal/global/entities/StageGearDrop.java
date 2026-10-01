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

    /**
     * Creates an empty {@link StageGearDrop} record.
     */
    public StageGearDrop() {
    }

    /**
     * Which stage number
     */
    public int getStage() {
        return stage;
    }

    /** Which stage number */
    public void setStage(int stage) {
        this.stage = stage;
    }

    /** Which map in stage number */
    public int getMap() {
        return map;
    }

    /** Which map in stage number */
    public void setMap(int map) {
        this.map = map;
    }

    /** Which Tier number of this stage hold (can be only \d+\.[0,5] as valid value) */
    public float getInitTier() {
        return initTier;
    }

    /** Which Tier number of this stage hold (can be only \d+\.[0,5] as valid value) */
    public void setInitTier(float initTier) {
        this.initTier = initTier;
    }

    /** Which gear type id from {@link io.github.zazalng.prickcal.global.contract.trickcal.GearType} */
    public int getLowGrade() {
        return lowGrade;
    }

    /** Which gear type id from {@link io.github.zazalng.prickcal.global.contract.trickcal.GearType} */
    public void setLowGrade(int lowGrade) {
        this.lowGrade = lowGrade;
    }

    /** Which gear type id from {@link io.github.zazalng.prickcal.global.contract.trickcal.GearType} */
    public int getHighGrade() {
        return highGrade;
    }

    /** Which gear type id from {@link io.github.zazalng.prickcal.global.contract.trickcal.GearType} */
    public void setHighGrade(int highGrade) {
        this.highGrade = highGrade;
    }

    /** Key record column require by API */
    public Long getId() {
        return id;
    }

    /** Key record column require by API */
    public void setId(Long id) {
        this.id = id;
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
