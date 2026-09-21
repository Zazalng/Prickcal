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
    HP((short) 1, "Health Point"),
    PDEF((short) 2, "Physical Defense"),
    CRIT((short) 3, "Crit Rate/Dmg"),
    MDEF((short) 4, "Magical Defense"),
    CRES((short) 5, "Crit Resistance"),
    PATK((short) 62, "Physical Attack"),
    MATK((short) 64, "Magical Attack"),
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

    public static GearType fromNo(short no) {
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
