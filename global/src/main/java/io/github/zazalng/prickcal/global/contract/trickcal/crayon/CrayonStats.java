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

import io.github.zazalng.prickcal.global.contract.EnumInterface;

/**
 * The stat a crayon slot raises, keyed by the stat number stored in a crayon line-up row.
 */
public enum CrayonStats implements EnumInterface {
    /**
     * Attack stat, number 1.
     */
    ATK((short) 1, "ATK"),
    /**
     * Health stat, number 2.
     */
    HP((short) 2, "HP"),
    /** Critical rate stat, number 3. */
    CRIT((short) 3, "Crit Rate"),
    /** Defense stat, number 4. */
    DEF((short) 4, "DEF"),
    /** Critical resistance stat, number 5. */
    CRES((short) 5, "Crit Resistance"),
    /**
     * Placeholder for a slot that is empty or holds an unrecognized stat number.
     */
    UNKNOWN((short) 0, "Unknown", false);

    /** The stat number as stored in a crayon line-up slot. */
    private final short no;
    /** The display name of the stat. */
    private final String name;
    /** Whether {@link #no} is a real stat number. */
    private final boolean valid;

    CrayonStats(short no, String name, boolean valid) {
        this.no = no;
        this.name = name;
        this.valid = valid;
    }

    CrayonStats(short no, String name) {
        this(no, name, true);
    }

    /**
     * Resolve the stat of a crayon slot from its stored number.
     * Only the five real stat numbers are matched; anything else, including the empty-slot
     * marker 0, resolves to {@link #UNKNOWN}.
     *
     * @param no the stat number read from a crayon line-up slot
     * @return the matching constant, or {@link #UNKNOWN} when no constant carries that number
     */
    public static CrayonStats fromNo(short no) {
        for (CrayonStats stat : CrayonStats.values()) {
            if (stat.no == no) {
                return stat;
            }
        }
        return UNKNOWN;
    }

    /**
     * The display name of this stat, also used as its select-menu label.
     *
     * @return the stat name, for example {@code "Crit Rate"}
     */
    public String getName() {
        return name;
    }

    /**
     * The number identifying this stat, the value {@link #fromNo(short)} looks up.
     *
     * @return the stat number; {@code 0} for {@link #UNKNOWN}
     */
    public short getNo() {
        return no;
    }

    /** {@inheritDoc} */
    @Override
    public String getOptionLabel() {
        return name;
    }

    /**
     * The stat number rendered as text for the Discord select menu, the value
     * {@link #fromNo(short)} accepts when the user picks it again.
     *
     * @return the stat number as a String
     */
    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }

    /**
     * Whether this constant is a real stat rather than the empty-slot placeholder.
     *
     * @return {@code false} only for {@link #UNKNOWN}
     */
    @Override
    public boolean isValid() {
        return valid;
    }
}
