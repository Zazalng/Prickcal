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

import io.github.zazalng.prickcal.global.contract.trickcal.EnumInterface;

public enum StarUpAmount implements EnumInterface {
    S8((short) 8, 60),
    S7((short) 7, 40),
    S6((short) 6, 0),
    S5((short) 5, 50),
    S4((short) 4, 25),
    S3((short) 3, 20),
    S2((short) 2, 12),
    S1((short) 1, 25);

    private final short star;
    private final int piece;
    private final boolean valid;

    StarUpAmount(short star, int piece, boolean valid) {
        this.star = star;
        this.piece = piece;
        this.valid = valid;
    }

    StarUpAmount(short star, int piece) {
        this(star, piece, true);
    }

    public static StarUpAmount fromStar(short star) {
        for (StarUpAmount s : StarUpAmount.values()) {
            if (star == s.star) return s;
        }

        return null;
    }

    public static int missingPiece(short currentStar, int apostleMax) {
        int missingPiece = 0;
        for (short i = (short) (currentStar+1); i <= apostleMax; i++) {
            StarUpAmount s = fromStar(i);
            if (s == null) break;
            missingPiece += s.getPiece();
        }
        return missingPiece;
    }

    public int getPiece() {
        return piece;
    }

    public short getStar() {
        return star;
    }

    @Override
    public String getOptionLabel() {
        return name();
    }

    @Override
    public String getOptionValue() {
        return String.valueOf(star);
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
