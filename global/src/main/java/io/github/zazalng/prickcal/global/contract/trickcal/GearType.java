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
package io.github.zazalng.prickcal.global.contract.trickcal;

import java.util.Objects;

public enum GearType {
    PDEF(1, "Pysical Defense"),
    MDEF(2, "Magical Defense"),
    CRIT(3, "Crit Rate/Dmg"),
    CRES(4, "Crit Resistance"),
    HP(5, "Health Point"),
    PATK(61, "Pysical Attack"),
    MATK(62, "Magical Attack"),
    UNKNOWN(0, "Invalid");

    private final int no;
    private final String name;

    GearType(int no, String name) {
        this.no = no;
        this.name = name;
    }

    public static GearType fromId(int no) {
        for(GearType g:GearType.values()){
            if (Objects.equals(no, g.no)) return g;
        }
        return UNKNOWN;
    }

    public int getNo() {
        return no;
    }

    public String getName() {
        return name;
    }
}
