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

/**
 * The cost of one crayon slot, keyed by the house depth the slot sits at in a crayon line-up.
 * The depth values match the per-slot depth list returned by the crayon line-up entity.
 */
public enum CrayonCosts {
    /**
     * A slot in house 1, costing 2 crayon to unlock 3 pieces.
     */
    HOUSE_1(1, 2, 3),
    /**
     * A slot in house 2, costing 4 crayon to unlock 4 pieces.
     */
    HOUSE_2(2, 4, 4),
    /** A slot in house 3, costing 6 crayon to unlock 5 pieces. */
    HOUSE_3(3, 6, 5),
    /** Placeholder for a depth that no house matches, carrying a negative price and amount. */
    UNKNOWN(0, -1, -1);

    /** The house depth of the slot, the value {@link #fromDepth(int)} looks up. */
    private final int depth;
    /** The crayon price needed to unlock the slot. */
    private final int price;
    /** The number of crayon pieces the slot grants once unlocked. */
    private final int amount;

    CrayonCosts(int depth, int price, int amount) {
        this.depth = depth;
        this.price = price;
        this.amount = amount;
    }

    /**
     * Resolve the cost of a crayon slot from the house depth stored in a crayon line-up.
     * The search covers every declared constant, so the depth 0 resolves to {@link #UNKNOWN}
     * itself; any other unmatched depth also falls back to {@link #UNKNOWN}.
     *
     * @param depth the house depth read from a crayon line-up slot
     * @return the matching constant, or {@link #UNKNOWN} when no constant carries that depth
     */
    public static CrayonCosts fromDepth(int depth) {
        for(CrayonCosts e: CrayonCosts.values()){
            if (e.depth == depth) return e;
        }

        return UNKNOWN;
    }

    /**
     * The crayon price needed to unlock the slot, the value a crayon total is summed from.
     *
     * @return the price in crayon; {@code -1} for the {@link #UNKNOWN} placeholder
     */
    public int getPrice() {
        return price;
    }

    /**
     * The number of crayon pieces granted by unlocking the slot.
     *
     * @return the piece count; {@code -1} for {@link #UNKNOWN}
     */
    public int getAmount() {
        return amount;
    }
}
