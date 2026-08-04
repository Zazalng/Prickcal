package io.github.zazalng.prickcal.global.entities;

import group.worldstandard.pudel.api.database.Column;
import group.worldstandard.pudel.api.database.Entity;

@Entity
public class StageGearDrop {
    @Column
    private Long id;
    @Column
    private int stage;
    @Column
    private int map;
    @Column
    private float initTier;
    @Column
    private int lowGrade;
    @Column
    private int highGrade;

    public StageGearDrop() {
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    public float getInitTier() {
        return initTier;
    }

    public void setInitTier(float initTier) {
        this.initTier = initTier;
    }

    public int getLowGrade() {
        return lowGrade;
    }

    public void setLowGrade(int lowGrade) {
        this.lowGrade = lowGrade;
    }

    public int getHighGrade() {
        return highGrade;
    }

    public void setHighGrade(int highGrade) {
        this.highGrade = highGrade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
