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

import java.util.Objects;

public enum StarUpAmount{
    S8(8, 60, null),
    S7(7, 40, S8),
    S6(6, 0, S7),
    S5(5, 50, S6),
    S4(4, 25, S5),
    S3(3, 20, S4),
    S2(2, 12, S3),
    S1(1, 25, S2);

    private final int star;
    private final int piece;
    private final StarUpAmount nextStar;

    StarUpAmount(int star, int piece, StarUpAmount nextStar) {
        this.star = star;
        this.piece = piece;
        this.nextStar = nextStar;
    }

    public static StarUpAmount fromStar(int star) {
        for (StarUpAmount s : StarUpAmount.values()) {
            if (Objects.equals(star, s.star)) return s;
        }

        return null;
    }

    public int getPiece() {
        return piece;
    }

    public int getStar() {
        return star;
    }

    public StarUpAmount getNextStar() {
        return nextStar;
    }
}
