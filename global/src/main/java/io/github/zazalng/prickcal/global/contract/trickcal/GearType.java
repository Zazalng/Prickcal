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

import io.github.zazalng.prickcal.global.contract.EnumInterface;

/**
 * The stat slot a piece of gear raises, keyed by the gear stat number used by the game's
 * stage-gear-drop data.
 */
public enum GearType implements EnumInterface {
    /**
     * Health point slot, number 1.
     */
    HP((short) 1, "Health Point"),
    /**
     * Physical defense slot, number 2.
     */
    PDEF((short) 2, "Physical Defense"),
    /** Critical rate and damage slot, number 3. */
    CRIT((short) 3, "Crit Rate/Dmg"),
    /** Magical defense slot, number 4. */
    MDEF((short) 4, "Magical Defense"),
    /** Critical resistance slot, number 5. */
    CRES((short) 5, "Crit Resistance"),
    /** Physical attack slot, number 62. */
    PATK((short) 62, "Physical Attack"),
    /** Magical attack slot, number 64. */
    MATK((short) 64, "Magical Attack"),
    /** Placeholder for a slot holding number 0 or a stat number this enum does not cover. */
    UNKNOWN((short) 0, "Invalid", false);

    /** The gear stat number as stored in the game data. */
    private final short no;
    /** The display name of the stat slot. */
    private final String name;
    /** Whether {@link #no} is a real gear stat number. */
    private final boolean valid;

    GearType(short no, String name, boolean valid) {
        this.no = no;
        this.name = name;
        this.valid = valid;
    }

    GearType(short no, String name) {
        this(no, name, true);
    }

    /**
     * Resolve the stat slot of a piece of gear from its stat number.
     * The search covers every declared constant, so the number 0 resolves to {@link #UNKNOWN}
     * itself; any other unmatched number also falls back to {@link #UNKNOWN}.
     *
     * @param no the gear stat number read from the game data
     * @return the matching constant, or {@link #UNKNOWN} when no constant carries that number
     */
    public static GearType fromNo(short no) {
        for(GearType g:GearType.values()){

            if (no == g.no) return g;
        }
        return UNKNOWN;
    }

    /**
     * The number identifying this stat slot, the value {@link #fromNo(short)} looks up.
     *
     * @return the gear stat number; {@code 0} for {@link #UNKNOWN}
     */
    public short getNo() {
        return no;
    }

    /**
     * The display name of this stat slot, also used as its select-menu label.
     *
     * @return the stat name, for example {@code "Physical Attack"}
     */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public String getOptionLabel() {
        return name;
    }

    /**
     * The gear stat number rendered as text for the Discord select menu, the value
     * {@link #fromNo(short)} accepts when the user picks it again.
     *
     * @return the gear stat number as a String
     */
    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }

    /**
     * Whether this constant is a real stat slot rather than the invalid placeholder.
     *
     * @return {@code false} only for {@link #UNKNOWN}
     */
    @Override
    public boolean isValid() {
        return valid;
    }
}
