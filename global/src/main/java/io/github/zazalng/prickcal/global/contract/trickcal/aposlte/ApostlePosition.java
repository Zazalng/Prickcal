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
 * The column an apostle stands in, keyed by the position number stored on an apostle row.
 */
public enum ApostlePosition implements DiscordEnumLabelInterface {
    /**
     * Front column, number 1.
     */
    FRONT((short) 1, "Front Column"),
    /**
     * Middle column, number 2.
     */
    MID((short) 2, "Mid Column"),
    /** Back column, number 3. */
    BACK((short) 3, "Back Column"),
    /** Round-robin formation, number 0. */
    ROBIN((short) 0, "Round Robin"),
    /** Placeholder for a position number this enum does not cover. */
    UNKNOWN((short) -1, "Invalid", false);

    /** The position number as stored on an apostle row. */
    private final short no;
    /** The display name of the seat the apostle occupies. */
    private final String seat;
    /** Whether {@link #no} is a real position number. */
    private final boolean valid;

    ApostlePosition(short no, String seat, boolean valid) {
        this.no = no;
        this.seat = seat;
        this.valid = valid;
    }

    ApostlePosition(short no, String seat) {
        this(no, seat, true);
    }

    /**
     * Resolve the position of an apostle from its stored position number.
     * The search covers every declared constant, so the numbers 0 and -1 resolve to
     * {@link #ROBIN} and {@link #UNKNOWN} respectively; any other unmatched number also falls
     * back to {@link #UNKNOWN}.
     *
     * @param no the position number read from an apostle row
     * @return the matching constant, or {@link #UNKNOWN} when no constant carries that number
     */
    public static ApostlePosition fromNo(short no) {
        for (ApostlePosition a : ApostlePosition.values()) {
            if (no == a.no) return a;
        }
        return UNKNOWN;
    }

    /**
     * The number identifying this position, the value {@link #fromNo(short)} looks up.
     *
     * @return the position number; {@code -1} for {@link #UNKNOWN}
     */
    public short getNo() {
        return no;
    }

    /**
     * The display name of the seat, also used as the select-menu label.
     *
     * @return the seat name, for example {@code "Front Column"}
     */
    public String getSeat() {
        return seat;
    }

    /** {@inheritDoc} */
    @Override
    public String getOptionLabel() {
        return seat;
    }

    /**
     * The position number rendered as text for the Discord select menu, the value
     * {@link #fromNo(short)} accepts when the user picks it again.
     *
     * @return the position number as a String
     */
    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }

    /**
     * Whether this constant is a real position rather than the invalid placeholder.
     *
     * @return {@code false} only for {@link #UNKNOWN}
     */
    @Override
    public boolean isValid() {
        return valid;
    }
}
