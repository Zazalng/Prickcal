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
package io.github.zazalng.prickcal.global.contract.operator;

import io.github.zazalng.prickcal.global.contract.DiscordEnumLabelInterface;

/**
 * An enum to document & describe which enum can do which thing
 */
public enum Operator implements DiscordEnumLabelInterface {
    /**
     * Highest rank; inherits every ability of {@link #ADMIN} and has no restriction of its own.
     * Stored value 0.
     */
    DEV((short) 0,
            "Inherit ability from `Admin`",
            "No restriction affected in this role."
    ),
    /**
     * Inherits every ability of {@link #EDITOR} and may create or delete any apostle and crayon
     * line-up row and any stage gear drop row. Stored value 1.
     */
    ADMIN((short) 1,
            "Inherit ability from `Editor`",
            "Create/Delete any row in Apostle record",
            "Create/Delete any row in CrayonLineUp record",
            "Update any row in Remarkable record",
            "Create/Delete any row in StageGearDrop record"
    ),
    /**
     * Inherits every ability of {@link #USER} and may update any apostle and crayon line-up row
     * and manage gift code and hashtag rows. Stored value 2.
     */
    EDITOR((short) 2,
            "Inherit ability from `User`",
            "Update any row in Apostle record",
            "Update any row in CrayonLineUp record",
            "Create/Update/Delete any row in GiftCode record",
            "Create/Update/Delete any row in HashTag record",
            "Update any row in StageGearDrop record"
    ),
    /**
     * The lowest rank, able to act only on the rows matching its own uid. Stored value 3.
     */
    USER((short) 3,
            "Update own uid match in Account record",
            "Update own uid match in ApostleTrack record",
            "Create/Update/Delete own uid match in CrayonRecord record",
            "Create/Delete own uid match in GiftAcquired record",
            "Create/Update/Delete own uid match in ApostleRemarkable record"
    ),
    /**
     * Placeholder for a stored operator value this enum does not cover; it performs no action at
     * all. Stored value -1.
     */
    UNKNOWN((short) -1, false,
            "Restrict to perform any action.",
            "Consider to report this to developer."
    );

    /**
     * The operator value persisted on an account row; lower means higher rank.
     */
    private final short value;
    /**
     * Whether this constant is a real operator level.
     */
    private final boolean valid;
    /** The bullet lines describing the abilities of this level. */
    private final String[] abilities;

    Operator(short value, boolean valid, String... abilities) {
        this.value = value;
        this.valid = valid;
        this.abilities = abilities;
    }

    Operator(short value, String... abilities) {
        this(value, true, abilities);
    }

    /**
     * Find the operator level new accounts start at.
     * The method walks every constant and keeps the highest value it sees, so it returns the
     * largest declared operator value, which is the value of {@link #USER}.
     *
     * @return the operator value to give a freshly created account
     */
    public static short defaultUser() {
        short i = 0;
        for (Operator o : Operator.values()) {
            if (o.value >= i) i = o.getValue();
        }
        return i;
    }

    /**
     * Resolve the operator level stored on an account row.
     * The search covers every declared constant, so the value -1 resolves to {@link #UNKNOWN}
     * itself; any other unmatched value also falls back to {@link #UNKNOWN}.
     *
     * @param operator the operator value read from an account row
     * @return the matching constant, or {@link #UNKNOWN} when no constant carries that value
     */
    public static Operator fromValue(short operator) {
        for (Operator o : Operator.values()) {
            if (o.value == operator) {
                return o;
            }
        }
        return UNKNOWN;
    }

    /**
     * The value identifying this operator level, the value {@link #fromValue(short)} looks up.
     * Lower values mean a higher rank, so a level satisfies a minimum check when its value is less
     * than or equal to the required one.
     *
     * @return the operator value, from {@code 0} for {@link #DEV} to {@code 3} for {@link #USER},
     *         and {@code -1} for {@link #UNKNOWN}
     */
    public short getValue() {
        return value;
    }

    /**
     * The constant name such as {@code "ADMIN"}, used as its select-menu label.
     *
     * @return the enum constant name
     */
    @Override
    public String getOptionLabel() {
        return name();
    }

    /**
     * The operator value rendered as text for the Discord select menu, the value
     * {@link #fromValue(short)} accepts when the user picks it again.
     *
     * @return the operator value as a String
     */
    @Override
    public String getOptionValue() {
        return String.valueOf(value);
    }

    /**
     * Whether this constant is a real operator level rather than the invalid placeholder.
     *
     * @return {@code false} only for {@link #UNKNOWN}
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    /**
     * Render the abilities of this level as a bullet list, one {@code "- "} prefixed line per
     * ability with the trailing newline stripped off the last line.
     *
     * @return the ability lines as a single multi-line String
     */
    public String getAbilities() {
        StringBuilder text = new StringBuilder();
        for (String s : abilities) {
            text.append("- %s\n".formatted(s));
        }
        return text.toString().stripTrailing();
    }
}
