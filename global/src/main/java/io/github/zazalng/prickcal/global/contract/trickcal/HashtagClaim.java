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

/**
 * The pole of a hashtag claim value, mapping the raw claim number stored on a hashtag row
 * to its positive, neutral or negative meaning.
 */
public enum HashtagClaim {
    /**
     * A claim below zero, counting against the hashtag.
     */
    NEGATIVE,
    /**
     * A claim of exactly zero, neither for nor against the hashtag.
     */
    NATURE,
    /** A claim above zero, counting in favour of the hashtag. */
    POSITIVE;

    /**
     * Resolve the pole of a raw hashtag claim number.
     * The mapping is by sign only, so every possible input resolves to a constant; the method
     * never returns {@code null} and has no invalid case.
     *
     * @param value the claim number stored on a hashtag row
     * @return {@link #POSITIVE} when {@code value} is greater than zero, {@link #NEGATIVE} when it
     *         is less than zero, and {@link #NATURE} when it is exactly zero
     */
    public static HashtagClaim fromValue(int value) {
        if(value > 0){
            return POSITIVE;
        } else if(value < 0){
            return NEGATIVE;
        } else {
            return NATURE;
        }
    }
}
