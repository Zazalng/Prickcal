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
package io.github.zazalng.prickcal.global.dto;

import io.github.zazalng.prickcal.global.entities.Apostle;

import java.util.ArrayList;
import java.util.List;

public final class ApostleSearch {
    private static final int PAGE_SIZE = 23;
    private final List<Short> sfRaceFilter;
    private final List<Short> sfColorFilter;
    private final List<Short> sfPositionFilter;
    private final List<Apostle> sfResult = new ArrayList<>();
    private String sfGuessName;
    // End index is exclusive
    private int sfStartIndex = 0;
    private int sfEndIndex = 23;

    /**
     * Create a search config from a guess name and every filter list.
     *
     * @param sfGuessName      a name to filter {@link Apostle} with
     * @param sfRaceFilter     the race value that must match
     * @param sfColorFilter    the color value that must match
     * @param sfPositionFilter the position value that must match
     */
    public ApostleSearch(String sfGuessName,
                         List<Short> sfRaceFilter,
                         List<Short> sfColorFilter,
                         List<Short> sfPositionFilter
    ) {
        this.sfGuessName = sfGuessName;
        this.sfRaceFilter = sfRaceFilter;
        this.sfColorFilter = sfColorFilter;
        this.sfPositionFilter = sfPositionFilter;
    }

    /**
     * Create a search config from a guess name, without any race, color or position filter.
     *
     * @param sfGuessName a name to filter {@link Apostle} with
     */
    public ApostleSearch(String sfGuessName) {
        this(sfGuessName, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    /**
     * A name to filter {@link Apostle} with
     */
    public String getSfGuessName() {
        return sfGuessName;
    }

    /**
     * Replace the name to filter {@link Apostle} with.
     *
     * @param sfGuessName the new name to filter with
     * @return this search config
     */
    public ApostleSearch setSfGuessName(String sfGuessName) {
        this.sfGuessName = sfGuessName;
        return this;
    }

    /** An inclusive start index of the current page. */
    public int getSfStartIndex() {
        return sfStartIndex;
    }

    /**
     * Move the start of the current page, never below 0.
     *
     * @param sfStartIndex the new inclusive start index
     * @return this search config
     */
    public ApostleSearch setSfStartIndex(int sfStartIndex) {
        this.sfStartIndex = Math.max(0, sfStartIndex);
        return this;
    }

    /** An exclusive end index of the current page. */
    public int getSfEndIndex() {
        return sfEndIndex;
    }

    /**
     * Move the end of the current page, never below the current {@link #getSfStartIndex()}.
     *
     * @param sfEndIndex the new exclusive end index
     * @return this search config
     */
    public ApostleSearch setSfEndIndex(int sfEndIndex) {
        this.sfEndIndex = Math.max(this.sfStartIndex, sfEndIndex);
        return this;
    }

    /** The race value that must match. */
    public List<Short> getSfRaceFilter() {
        return sfRaceFilter;
    }

    /** The color value that must match. */
    public List<Short> getSfColorFilter() {
        return sfColorFilter;
    }

    /** The position value that must match. */
    public List<Short> getSfPositionFilter() {
        return sfPositionFilter;
    }

    /** An apostle that match this search, used as the paging source. */
    public List<Apostle> getSfResult() {
        return sfResult;
    }

    /** Whether the current page is not the first one. */
    public boolean hasPreviousPage() {
        return sfStartIndex > 0;
    }

    /** Whether {@link #getSfResult()} still hold an apostle after the current page. */
    public boolean hasNextPage() {
        return sfEndIndex < sfResult.size();
    }

    /**
     * Move the current page forward by one page size, stopping at the end of {@link #getSfResult()}.
     *
     * @return this search config
     */
    public ApostleSearch nextPage() {
        sfStartIndex = sfEndIndex;
        sfEndIndex = Math.min(
                sfStartIndex + PAGE_SIZE,
                sfResult.size()
        );

        return this;
    }

    /**
     * Move the current page backward by one page size, stopping at the first page.
     *
     * @return this search config
     */
    public ApostleSearch previousPage() {
        sfEndIndex = sfStartIndex;
        sfStartIndex = Math.max(
                0,
                sfStartIndex - PAGE_SIZE
        );

        return this;
    }
}