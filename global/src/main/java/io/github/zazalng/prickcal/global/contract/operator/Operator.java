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
    /**
     * A Level that had ability to
     * <ul>
     *     <li>inherit ability from Operator that had its value + 1</li>
     *     <li>Create/Delete any row in Apostle record</li>
     *     <li>Create/Delete any row in CrayonLineUp record</li>
     *     <li>Update any row in Remarkable record</li>
     *     <li>Create/Delete any row in StageGearDrop record</li>
     * </ul>
     */
    ADMIN((short) 0),
    /**
     * A Level that had ability to
     * <ul>
     *     <li>inherit ability from Operator that had its value + 1</li>
     *     <li>Update any row in Apostle record</li>
     *     <li>Update any row in CrayonLineUp record</li>
     *     <li>Create/Update/Delete any row in GiftCode record</li>
     *     <li>Create/Update/Delete any row in HashTag record</li>
     *     <li>Update any row in StageGearDrop record</li>
     * </ul>
     */
    EDITOR((short) 1),
    /**
     * A Level that had ability to
     * <ul>
     *     <li>Update own uid match in Account record</li>
     *     <li>Update own uid match in ApostleTrack record</li>
     *     <li>Create/Update/Delete own uid match in CrayonRecord record</li>
     *     <li>Create/Delete own uid match in GiftAcquired record</li>
     *     <li>Create/Update/Delete own uid match in ApostleRemarkable record</li>
     * </ul>
     */
    USER((short) 2),
    /**
     * A level that had 0 ability to do anything
     */
    UNKNOWN((short) -1, false);

    private final short value;
    private final boolean valid;

    Operator(short value, boolean valid) {
        this.value = value;
        this.valid = valid;
    }

    Operator(short value) {
        this(value, true);
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
}
