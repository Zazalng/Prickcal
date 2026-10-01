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

import io.github.zazalng.prickcal.global.contract.EnumInterface;

import java.awt.*;

/**
 * The personality color of an apostle, keyed by the color number stored on an apostle row,
 * each carrying the display color used when rendering the apostle.
 */
public enum ApostleColor implements EnumInterface {
    /**
     * Placeholder for a color number this enum does not cover, drawn in black.
     */
    UNKNOWN((short) -1, "Invalid", new Color(0, 0, 0), false),
    /**
     * Rainbow personality, number 0, drawn in white.
     */
    RAINBOW((short) 0, "Rainbow", new Color(255, 255, 255)),
    /** Innocent personality, number 1, drawn in green. */
    GREEN((short) 1, "Innocent", new Color(80, 210, 80)),
    /** Composed personality, number 2, drawn in cyan. */
    TEAL((short) 2, "Composed", new Color(0, 250, 255)),
    /** Mad personality, number 3, drawn in red. */
    RED((short) 3, "Mad", new Color(210, 0, 0)),
    /** Vivacious personality, number 4, drawn in yellow. */
    YELLOW((short) 4, "Vivacious", new Color(255, 255, 0)),
    /** Depressed personality, number 5, drawn in purple. */
    PURPLE((short) 5, "Depressed", new Color(155, 55, 255)),
    //GT((short) 12, "Innocent / Composed", new Color(80, 205, 75))
    //GR((short) 13, "Innocent / Madness", new Color(35, 210, 80))
    //GY((short) 14, "Innocent / Vivacious", new Color(80,210,80))
    /** Dual personality of innocent and depressed, number 15, drawn in pink. */
    GP((short) 15, "Innocent / Depressed", new Color(235, 10, 75))
    //TR((short) 23, "Composed / Madness", new Color(210, 250, 255))
    //TY((short) 24, "Composed / Vivacious", new Color(255, 250, 255))
    //TP((short) 25, "Composed / Depressed", new Color(155, 50, 255))
    //RY((short) 34, "Madness / Vivacious", new Color(210, 255, 0))
    //RP((short) 35, "Madness / Depressed", new Color(110, 55, 255))
    //YP((short) 45, "Vivacious / Depressed", new Color(155, 55, 255))
    ;

    /** The color number as stored on an apostle row. */
    private final short no;
    /** The display name of the personality. */
    private final String personality;
    /** The RGB color the personality is drawn with. */
    private final Color color;
    /** Whether {@link #no} is a real personality number. */
    private final boolean valid;

    ApostleColor(short no, String personality, Color color, boolean valid) {
        this.no = no;
        this.personality = personality;
        this.color = color;
        this.valid = valid;
    }

    ApostleColor(short no, String personality, Color color) {
        this(no, personality, color, true);
    }

    /**
     * Resolve the personality color of an apostle from its stored color number.
     * The search covers every declared constant, so the number -1 resolves to {@link #UNKNOWN}
     * itself; any other unmatched number, including the commented-out dual combinations, also
     * falls back to {@link #UNKNOWN}.
     *
     * @param no the color number read from an apostle row
     * @return the matching constant, or {@link #UNKNOWN} when no constant carries that number
     */
    public static ApostleColor fromNo(short no) {
        for (ApostleColor a : ApostleColor.values()) {
            if (no == a.no) return a;
        }
        return UNKNOWN;
    }

    /**
     * The number identifying this personality, the value {@link #fromNo(short)} looks up.
     *
     * @return the color number; {@code -1} for {@link #UNKNOWN}
     */
    public short getNo() {
        return no;
    }

    /**
     * The display name of the personality, also used as the select-menu label.
     *
     * @return the personality name, for example {@code "Innocent"}
     */
    public String getPersonality() {
        return personality;
    }

    /**
     * The RGB color this personality is drawn with.
     *
     * @return the display color; black for {@link #UNKNOWN}
     */
    public Color getColor() {
        return color;
    }

    /** {@inheritDoc} */
    @Override
    public String getOptionLabel() {
        return personality;
    }

    /**
     * The color number rendered as text for the Discord select menu, the value
     * {@link #fromNo(short)} accepts when the user picks it again.
     *
     * @return the color number as a String
     */
    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }

    /**
     * Whether this constant is a real personality rather than the invalid placeholder.
     *
     * @return {@code false} only for {@link #UNKNOWN}
     */
    @Override
    public boolean isValid() {
        return valid;
    }
}
