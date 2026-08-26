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

public enum GearType {
    PATK(1),
    MATK(2),
    PDEF(3),
    MDEF(4),
    CRIT(5),
    CRES(6),
    HP(7),
    UNKNOWN(0);

    private final int id;

    GearType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static GearType fromId(int id){
        for(GearType g:GearType.values()){
            if(g.id == id) return g;
        }
        return UNKNOWN;
    }
}
