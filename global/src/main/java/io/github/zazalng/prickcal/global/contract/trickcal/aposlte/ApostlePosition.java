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

public enum ApostlePosition implements EnumInterface {
    FRONT((short) 1, "Front Column"),
    MID((short) 2, "Mid Column"),
    BACK((short) 3, "Back Column"),
    ROBIN((short) 0, "Round Robin"),
    UNKNOWN((short) -1, "Invalid");

    private final short no;
    private final String seat;

    ApostlePosition(short no, String seat) {
        this.no = no;
        this.seat = seat;
    }

    public static ApostlePosition fromNo(short no) {
        for (ApostlePosition a : ApostlePosition.values()) {
            if (no == a.no) return a;
        }
        return UNKNOWN;
    }

    public short getNo() {
        return no;
    }

    public String getSeat() {
        return seat;
    }

    @Override
    public String getOptionLabel() {
        return seat;
    }

    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }
}
