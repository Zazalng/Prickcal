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

/**
 * The race of an apostle, keyed by the race number stored on an apostle row.
 */
public enum ApostleRace implements EnumInterface {
    /**
     * Placeholder for a race number this enum does not cover.
     */
    UNKNOWN((short) 0, "Invalid", false),
    /**
     * Sprite race, number 1.
     */
    SPRITE((short) 1, "Sprite"),
    /** Elemental race, number 2. */
    ELEMENTAL((short) 2, "Elemental"),
    /** Werebeast race, number 3. */
    BEASTMEN((short) 3, "Werebeast"),
    /** Dragon race, number 4. */
    DRAGON((short) 4, "Dragon"),
    /** Phantom race, number 5. */
    PHANTOM((short) 5, "Phantom"),
    /** Elf race, number 6. */
    ELF((short) 6, "Elf"),
    /** Witch race, number 7. */
    WITCH((short) 7, "Witch"),
    /** Mystic race, number 8. */
    MYSTIC((short) 8, "Mystic");

    /** The race number as stored on an apostle row. */
    private final short no;
    /** The display name of the race. */
    private final String name;
    /** Whether {@link #no} is a real race number. */
    private final boolean valid;

    ApostleRace(short no, String name, boolean valid) {
        this.no = no;
        this.name = name;
        this.valid = valid;
    }

    ApostleRace(short no, String name) {
        this(no, name, true);
    }

    /**
     * Resolve the race of an apostle from its stored race number.
     * The search covers every declared constant, so the number 0 resolves to {@link #UNKNOWN}
     * itself; any other unmatched number also falls back to {@link #UNKNOWN}.
     *
     * @param no the race number read from an apostle row
     * @return the matching constant, or {@link #UNKNOWN} when no constant carries that number
     */
    public static ApostleRace fromNo(short no) {
        for (ApostleRace race : ApostleRace.values()) {
            if (race.no == no) return race;
        }
        return UNKNOWN;
    }

    /**
     * The number identifying this race, the value {@link #fromNo(short)} looks up.
     *
     * @return the race number; {@code 0} for {@link #UNKNOWN}
     */
    public int getNo() {
        return no;
    }

    /**
     * The display name of this race, also used as its select-menu label.
     *
     * @return the race name, for example {@code "Werebeast"}
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
     * The race number rendered as text for the Discord select menu, the value
     * {@link #fromNo(short)} accepts when the user picks it again.
     *
     * @return the race number as a String
     */
    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }

    /**
     * Whether this constant is a real race rather than the invalid placeholder.
     *
     * @return {@code false} only for {@link #UNKNOWN}
     */
    @Override
    public boolean isValid() {
        return valid;
    }
}
