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

import io.github.zazalng.prickcal.global.contract.trickcal.EnumInterface;

/**
 * An enum to document & describe which enum can do which thing
 */
public enum Operator implements EnumInterface {
    DEV((short) 0,
            "Inherit ability from `Admin`",
            "No restriction affected in this role."
    ),
    ADMIN((short) 1,
            "Inherit ability from `Editor`",
            "Create/Delete any row in Apostle record",
            "Create/Delete any row in CrayonLineUp record",
            "Update any row in Remarkable record",
            "Create/Delete any row in StageGearDrop record"
    ),
    EDITOR((short) 2,
            "Inherit ability from `User`",
            "Update any row in Apostle record",
            "Update any row in CrayonLineUp record",
            "Create/Update/Delete any row in GiftCode record",
            "Create/Update/Delete any row in HashTag record",
            "Update any row in StageGearDrop record"
    ),
    USER((short) 3,
            "Update own uid match in Account record",
            "Update own uid match in ApostleTrack record",
            "Create/Update/Delete own uid match in CrayonRecord record",
            "Create/Delete own uid match in GiftAcquired record",
            "Create/Update/Delete own uid match in ApostleRemarkable record"
    ),
    UNKNOWN((short) -1, false, "Restrict to perform any action.");

    private final short value;
    private final boolean valid;
    private final String[] abilities;

    Operator(short value, boolean valid, String... abilities) {
        this.value = value;
        this.valid = valid;
        this.abilities = abilities;
    }

    Operator(short value, String... abilities) {
        this(value, true, abilities);
    }

    public static short defaultUser() {
        short i = 0;
        for (Operator o : Operator.values()) {
            if (o.value >= i) i = o.getValue();
        }
        return i;
    }

    public static Operator fromValue(short operator) {
        for (Operator o : Operator.values()) {
            if (o.value == operator) {
                return o;
            }
        }
        return UNKNOWN;
    }

    public short getValue() {
        return value;
    }

    @Override
    public String getOptionLabel() {
        return name();
    }

    @Override
    public String getOptionValue() {
        return String.valueOf(value);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public String getAbilities() {
        StringBuilder text = new StringBuilder();
        for (String s : abilities) {
            text.append("- %s\n".formatted(s));
        }
        return text.toString().stripTrailing();
    }
}
