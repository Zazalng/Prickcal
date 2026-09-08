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
    private int house1A;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private int house1B;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private int house2A;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private int house2B;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private int house2C;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private int house3A;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private int house3B;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private int house3C;
    /**
     * Value that relate with {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    @Column(defaultValue = "0")
    private int house3D;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getHouse1A() {
        return house1A;
    }

    public void setHouse1A(int house1A) {
        this.house1A = house1A;
    }

    public int getHouse1B() {
        return house1B;
    }

    public void setHouse1B(int house1B) {
        this.house1B = house1B;
    }

    public int getHouse2A() {
        return house2A;
    }

    public void setHouse2A(int house2A) {
        this.house2A = house2A;
    }

    public int getHouse2B() {
        return house2B;
    }

    public void setHouse2B(int house2B) {
        this.house2B = house2B;
    }

    public int getHouse2C() {
        return house2C;
    }

    public void setHouse2C(int house2C) {
        this.house2C = house2C;
    }

    public int getHouse3A() {
        return house3A;
    }

    public void setHouse3A(int house3A) {
        this.house3A = house3A;
    }

    public int getHouse3B() {
        return house3B;
    }

    public void setHouse3B(int house3B) {
        this.house3B = house3B;
    }

    public int getHouse3C() {
        return house3C;
    }

    public void setHouse3C(int house3C) {
        this.house3C = house3C;
    }

    public int getHouse3D() {
        return house3D;
    }

    public void setHouse3D(int house3D) {
        this.house3D = house3D;
    }

    /**
     * A line-up of crayon must always return in this standard
     * {@code {getHouse1A(), getHouse1B(), getHouse2A(), getHouse2B(), getHouse2C(), getHouse3A(), getHouse3B(), getHouse3C(), getHouse3D()}}
     *
     * @return Line Up Array
     */
    public List<Integer> getLineUp() {
        return Arrays.asList(getHouse1A(), getHouse1B(), getHouse2A(), getHouse2B(), getHouse2C(), getHouse3A(), getHouse3B(), getHouse3C(), getHouse3D());
    }

    /**
     * Check if any Line-Up contain value of 0 which is mark for Invalid in {@link io.github.zazalng.prickcal.global.contract.trickcal.crayon.CrayonStats}
     */
    public boolean isValid() {
        return getLineUp().contains(0);
    }
}
