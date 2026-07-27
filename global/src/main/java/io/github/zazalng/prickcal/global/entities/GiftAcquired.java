package io.github.zazalng.prickcal.global.entities;

import group.worldstandard.pudel.api.database.Column;

public class GiftAcquired {
    @Column
    private Long id;

    @Column
    private String uid;

    @Column
    private Long codeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
