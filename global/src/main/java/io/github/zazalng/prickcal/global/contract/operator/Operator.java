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

/**
 * An enum to document & describe which enum can do which thing
 */
public enum Operator {
    /**
     * A Level that had ability to
     * <ul>
     *     <li>inherit ability from Operator that had its value + 1</li>
     *     <li>Create/Delete any row in Apostle record</li>
     *     <li>Create/Delete any row in CrayonLineUp record</li>
     *     <li>Create any row in GiftCode record</li>
     *     <li>Create any row in HashTag record</li>
     *     <li>Update any row in Remarkable record</li>
     *     <li>Create/Delete any row in StageGearDrop record</li>
     * </ul>
     */
    ADMIN(0),
    /**
     * A Level that had ability to
     * <ul>
     *     <li>inherit ability from Operator that had its value + 1</li>
     *     <li>Update any row in Apostle record</li>
     *     <li>Update any row in CrayonLineUp record</li>
     *     <li>Update/Delete any row in GiftCode record</li>
     *     <li>Update/Delete any row in HashTag record</li>
     *     <li>Update any row in StageGearDrop record</li>
     * </ul>
     */
    EDITOR(1),
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
    USER(2),
    /**
     * A level that had 0 ability to do anything
     */
    UNKNOWN(-1);

    private final int value;

    Operator(int value) {
        this.value = value;
    }

    public static Operator fromString(int operator) {
        for (Operator o : Operator.values()) {
            if (o.value == operator) {
                return o;
            }
        }
        return UNKNOWN;
    }
}
