package io.github.zazalng.prickcal.global.entities;

import group.worldstandard.pudel.api.database.Column;
import group.worldstandard.pudel.api.database.Entity;

@Entity
public class ApostleTrack {
    @Column
    private Long id;

    @Column
    private Long apostleId;

    @Column
    private String uid;

    @Column
    private int currentStar;

    @Column
    private String crayon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApostleId() {
        return apostleId;
    }

    public void setApostleId(Long apostleId) {
        this.apostleId = apostleId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCurrentStar() {
        return currentStar;
    }

    public void setCurrentStar(int star) {
        this.currentStar = star;
    }

    public String getCrayon() {
        return crayon;
    }

    public void setCrayon(String crayon) {
        this.crayon = crayon;
    }
}
