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
import io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonCosts;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "crayon_line_ups")
public class CrayonLineUp {
    /**
     * Key record column require by API (as well as using for tracking from {@link Apostle}.crayon)
     */
    @Column
    private Long id;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private short house1A;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private short house1B;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private short house2A;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private short house2B;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private short house2C;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private short house3A;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private short house3B;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private short house3C;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private short house3D;

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

    public short getHouse1A() {
        return house1A;
    }

    public void setHouse1A(short house1A) {
        this.house1A = house1A;
    }

    public short getHouse1B() {
        return house1B;
    }

    public void setHouse1B(short house1B) {
        this.house1B = house1B;
    }

    public short getHouse2A() {
        return house2A;
    }

    public void setHouse2A(short house2A) {
        this.house2A = house2A;
    }

    public short getHouse2B() {
        return house2B;
    }

    public void setHouse2B(short house2B) {
        this.house2B = house2B;
    }

    public short getHouse2C() {
        return house2C;
    }

    public void setHouse2C(short house2C) {
        this.house2C = house2C;
    }

    public short getHouse3A() {
        return house3A;
    }

    public void setHouse3A(short house3A) {
        this.house3A = house3A;
    }

    public short getHouse3B() {
        return house3B;
    }

    public void setHouse3B(short house3B) {
        this.house3B = house3B;
    }

    public short getHouse3C() {
        return house3C;
    }

    public void setHouse3C(short house3C) {
        this.house3C = house3C;
    }

    public short getHouse3D() {
        return house3D;
    }

    public void setHouse3D(short house3D) {
        this.house3D = house3D;
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

    /**
     * A line-up of crayon must always return in this standard
     * {@code {getHouse1A(), getHouse1B(), getHouse2A(), getHouse2B(), getHouse2C(), getHouse3A(), getHouse3B(), getHouse3C(), getHouse3D()}}
     *
     * @return Line Up Array
     */
    public List<Short> getLineUp() {
        return Arrays.asList(getHouse1A(), getHouse1B(), getHouse2A(), getHouse2B(), getHouse2C(), getHouse3A(), getHouse3B(), getHouse3C(), getHouse3D());
    }

    /**
     * A line-up depth of crayon must depth with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonCosts}
     *
     * @return Depth Array match House Level
     */
    public List<Integer> getDepth() {
        return Arrays.asList(1, 1, 2, 2, 2, 3, 3, 3, 3);
    }

    public int totalCost() {
        int totalCost = 0;
        for (int i = 0; i < getLineUp().size(); i++) {
            totalCost += CrayonCosts.fromDepth(getDepth().get(i)).getPrice();
        }
        return totalCost;
    }

    /**
     * Check if any Line-Up contain value of 0 which is mark for Invalid in {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    public boolean isValid() {
        return !getLineUp().contains((short) 0);
    }
}
