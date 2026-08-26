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

public enum ApostleRace {
    UNKNOWN(0, "Unknown"),
    SPRITE(1, "Sprite"),
    ELEMENTAL(2, "Elemental"),
    BEASTMEN(3, "Beastmen"),
    DRAGON(4, "Dragon"),
    PHANTOM(5, "Phantom"),
    ELF(6, "Elf"),
    WITCH(7, "Witch"),
    MYSTIC(8, "Mystic");

    private final int no;
    private final String name;

    ApostleRace(int no, String name) {
        this.no = no;
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public static ApostleRace fromNo(int no) {
        for (ApostleRace race : ApostleRace.values()) {
            if (race.getNo() == no) {
                return race;
            }
        }
        return UNKNOWN;
    }
}
