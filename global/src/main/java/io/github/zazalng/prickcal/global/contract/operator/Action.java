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

import java.util.Objects;

public enum Action {
    /**
     * Any action that result to create new row from any table
     */
    CREATE(1),
    /**
     * Any action that result to any row to change data
     */
    UPDATE(0),
    /**
     * Any action that result to delete row from any table
     */
    DELETE(-1),
    /**
     * Any action that getting this result must reject an action
     */
    UNKNOWN(null);

    private final Integer val;

    Action(Integer i){
        this.val = i;
    }

    public static Action fromValue(Integer i) {
        for (Action a : Action.values()) {
            if (Objects.equals(a.val, i)) {
                return a;
            }
        }
        return UNKNOWN;
    }
}
