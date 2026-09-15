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
package io.github.zazalng.prickcal.global.contract.trickcal.aposlte;

import io.github.zazalng.prickcal.global.contract.trickcal.EnumInterface;

public enum ApostleRace implements EnumInterface {
    UNKNOWN((short) 0, "Invalid"),
    SPRITE((short) 1, "Sprite"),
    ELEMENTAL((short) 2, "Elemental"),
    BEASTMEN((short) 3, "Werebeast"),
    DRAGON((short) 4, "Dragon"),
    PHANTOM((short) 5, "Phantom"),
    ELF((short) 6, "Elf"),
    WITCH((short) 7, "Witch"),
    MYSTIC((short) 8, "Mystic");

    private final short no;
    private final String name;

    ApostleRace(short no, String name) {
        this.no = no;
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public static ApostleRace fromNo(short no) {
        for (ApostleRace race : ApostleRace.values()) {
            if (race.no == no) return race;
        }
        return UNKNOWN;
    }

    @Override
    public String getOptionLabel() {
        return name;
    }

    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }
}
