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

    public ApostleSearch(String sfGuessName) {
        this.sfGuessName = sfGuessName;
        this.sfRaceFilter = new ArrayList<>();
        this.sfColorFilter = new ArrayList<>();
        this.sfPositionFilter = new ArrayList<>();
    }

    public String getSfGuessName() {
        return sfGuessName;
    }

    public ApostleSearch setSfGuessName(String sfGuessName) {
        this.sfGuessName = sfGuessName;
        return this;
    }

    public int getSfStartIndex() {
        return sfStartIndex;
    }

    public ApostleSearch setSfStartIndex(int sfStartIndex) {
        this.sfStartIndex = Math.max(0, sfStartIndex);
        return this;
    }

    public int getSfEndIndex() {
        return sfEndIndex;
    }

    public ApostleSearch setSfEndIndex(int sfEndIndex) {
        this.sfEndIndex = Math.max(this.sfStartIndex, sfEndIndex);
        return this;
    }

    public List<Short> getSfRaceFilter() {
        return sfRaceFilter;
    }

    public List<Short> getSfColorFilter() {
        return sfColorFilter;
    }

    public List<Short> getSfPositionFilter() {
        return sfPositionFilter;
    }

    public List<Apostle> getSfResult() {
        return sfResult;
    }

    public boolean hasPreviousPage() {
        return sfStartIndex > 0;
    }

    public boolean hasNextPage() {
        return sfEndIndex < sfResult.size();
    }

    public ApostleSearch nextPage() {
        sfStartIndex = sfEndIndex;
        sfEndIndex = Math.min(
                sfStartIndex + PAGE_SIZE,
                sfResult.size()
        );

        return this;
    }

    public ApostleSearch previousPage() {
        sfEndIndex = sfStartIndex;
        sfStartIndex = Math.max(
                0,
                sfStartIndex - PAGE_SIZE
        );

        return this;
    }
}