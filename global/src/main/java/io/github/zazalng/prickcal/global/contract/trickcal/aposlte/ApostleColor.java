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

import java.awt.*;

public enum ApostleColor implements EnumInterface {
    UNKNOWN((short) -1, "Invalid", new Color(0, 0, 0)),
    RAINBOW((short) 0, "Rainbow", new Color(255, 255, 255)),
    GREEN((short) 1, "Innocent", new Color(80, 210, 80)),
    TEAL((short) 2, "Composed", new Color(0, 250, 255)),
    RED((short) 3, "Mad", new Color(210, 0, 0)),
    YELLOW((short) 4, "Vivacious", new Color(255, 255, 0)),
    PURPLE((short) 5, "Depressed", new Color(155, 55, 255));

    private final short no;
    private final String personality;
    private final Color color;

    ApostleColor(short no, String personality, Color color) {
        this.no = no;
        this.personality = personality;
        this.color = color;
    }

    public static ApostleColor fromNo(short no) {
        for (ApostleColor a : ApostleColor.values()) {
            if (no == a.no) return a;
        }
        return UNKNOWN;
    }

    public short getNo() {
        return no;
    }

    public String getPersonality() {
        return personality;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String getOptionLabel() {
        return personality;
    }

    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }
}
