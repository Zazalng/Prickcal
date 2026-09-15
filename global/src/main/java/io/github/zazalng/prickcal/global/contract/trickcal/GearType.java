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

public enum GearType implements EnumInterface {
    PDEF((short) 1, "Physical Defense"),
    MDEF((short) 2, "Magical Defense"),
    CRIT((short) 3, "Crit Rate/Dmg"),
    CRES((short) 4, "Crit Resistance"),
    HP((short) 5, "Health Point"),
    PATK((short) 61, "Physical Attack"),
    MATK((short) 62, "Magical Attack"),
    UNKNOWN((short) 0, "Invalid", false);

    private final short no;
    private final String name;
    private final boolean valid;

    GearType(short no, String name, boolean valid) {
        this.no = no;
        this.name = name;
        this.valid = valid;
    }

    GearType(short no, String name) {
        this(no, name, true);
    }

    public static GearType fromId(short no) {
        for(GearType g:GearType.values()){
            if (no == g.no) return g;
        }
        return UNKNOWN;
    }

    public short getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getOptionLabel() {
        return name;
    }

    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
