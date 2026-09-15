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
package io.github.zazalng.prickcal.global.contract.trickcal.crayon;

import io.github.zazalng.prickcal.global.contract.trickcal.EnumInterface;

public enum CrayonStats implements EnumInterface {
    ATK((short) 1, "ATK"),
    HP((short) 2, "HP"),
    CRIT((short) 3, "Crit Rate"),
    DEF((short) 4, "DEF"),
    CRES((short) 5, "Crit Resistance"),
    UNKNOWN((short) 0, "Unknown", false);

    private final short no;
    private final String name;
    private final boolean valid;

    CrayonStats(short no, String name, boolean valid) {
        this.no = no;
        this.name = name;
        this.valid = valid;
    }

    CrayonStats(short no, String name) {
        this(no, name, true);
    }

    public static CrayonStats fromNo(short no) {
        for (CrayonStats stat : CrayonStats.values()) {
            if (stat.no == no) {
                return stat;
            }
        }
        return UNKNOWN;
    }

    public String getName() {
        return name;
    }

    public short getNo() {
        return no;
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
