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

public enum CrayonCosts {
    HOUSE_1((short) 1, 2, 3),
    HOUSE_2((short) 2, 4, 4),
    HOUSE_3((short) 3, 6, 5),
    UNKNOWN((short) 0, -1, -1);

    private final short depth;
    private final int price;
    private final int amount;

    CrayonCosts(short depth, int price, int amount) {
        this.depth = depth;
        this.price = price;
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public static CrayonCosts fromDepth(short depth) {
        for(CrayonCosts e: CrayonCosts.values()){
            if (e.depth == depth) return e;
        }

        return UNKNOWN;
    }
}
