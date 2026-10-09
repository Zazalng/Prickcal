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

import io.github.zazalng.prickcal.global.contract.DiscordEnumLabelInterface;

/**
 * The certificate pieces needed to raise an apostle by one star, keyed by the target star
 * number from 1 up to the maximum star an apostle can reach.
 */
public enum StarUpAmount implements DiscordEnumLabelInterface {
    /**
     * Raising to star 8 costs 60 pieces.
     */
    S8((short) 8, 60),
    /**
     * Raising to star 7 costs 40 pieces.
     */
    S7((short) 7, 40),
    /** Raising to star 6 costs 0 pieces. */
    S6((short) 6, 0),
    /** Raising to star 5 costs 50 pieces. */
    S5((short) 5, 50),
    /** Raising to star 4 costs 25 pieces. */
    S4((short) 4, 25),
    /** Raising to star 3 costs 20 pieces. */
    S3((short) 3, 20),
    /** Raising to star 2 costs 12 pieces. */
    S2((short) 2, 12),
    /** Raising to star 1 costs 25 pieces. */
    S1((short) 1, 25);

    /** The target star number, the value {@link #fromStar(short)} looks up. */
    private final short star;
    /** The number of certificate pieces the raise costs. */
    private final int piece;
    /** Whether this constant is a real star level. */
    private final boolean valid;

    StarUpAmount(short star, int piece, boolean valid) {
        this.star = star;
        this.piece = piece;
        this.valid = valid;
    }

    StarUpAmount(short star, int piece) {
        this(star, piece, true);
    }

    /**
     * Resolve the cost of raising an apostle to a given star.
     * Every declared constant marks itself valid, so the lookup has no invalid case of its own;
     * a star number this enum does not declare, such as 0 or a value above 8, yields
     * {@code null} rather than a placeholder constant.
     *
     * @param star the target star number
     * @return the matching constant, or {@code null} when no constant carries that star number
     */
    public static StarUpAmount fromStar(short star) {
        for (StarUpAmount s : StarUpAmount.values()) {
            if (star == s.star) return s;
        }

        return null;
    }

    /**
     * Sum the pieces still needed to bring an apostle from its current star up to its maximum.
     * The loop walks the stars above {@code currentStar} up to and including {@code apostleMax}
     * and adds each level's {@link #getPiece() piece} cost; it stops early and returns the partial
     * sum when a star in that range has no constant, which happens once the range passes star 8
     * and {@code apostleMax} exceeds the highest known level.
     *
     * @param currentStar the star the apostle currently holds
     * @param apostleMax the maximum star the apostle can reach
     * @return the total missing piece count, zero when the apostle is already at its maximum or
     *         the current star is above it
     */
    public static int missingPiece(short currentStar, int apostleMax) {
        int missingPiece = 0;
        for (short i = (short) (currentStar+1); i <= apostleMax; i++) {
            StarUpAmount s = fromStar(i);
            if (s == null) break;
            missingPiece += s.getPiece();
        }
        return missingPiece;
    }

    /**
     * The number of certificate pieces needed to reach this star.
     *
     * @return the piece cost of the raise
     */
    public int getPiece() {
        return piece;
    }

    /**
     * The star number this constant prices, the value {@link #fromStar(short)} looks up.
     *
     * @return the target star number, from 1 to 8
     */
    public short getStar() {
        return star;
    }

    /**
     * The constant name such as {@code "S5"}, used as its select-menu label.
     *
     * @return the enum constant name
     */
    @Override
    public String getOptionLabel() {
        return name();
    }

    /**
     * The star number rendered as text for the Discord select menu, the value
     * {@link #fromStar(short)} accepts when the user picks it again.
     *
     * @return the star number as a String
     */
    @Override
    public String getOptionValue() {
        return String.valueOf(star);
    }

    /**
     * Whether this constant is a real star level. Every declared constant is valid, since this
     * enum has no placeholder member.
     *
     * @return {@code true} for every constant
     */
    @Override
    public boolean isValid() {
        return valid;
    }
}
