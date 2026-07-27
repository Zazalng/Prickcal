package io.github.zazalng.prickcal.global.entities;

import group.worldstandard.pudel.api.database.Column;
import group.worldstandard.pudel.api.database.Entity;

@Entity
public class User {
    @Column
    private Long id;

    @Column
    private String uid;

    @Column
    private int ops;

    @Column
    private boolean leak;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getOps() {
        return ops;
    }

    public void setOps(int ops) {
        this.ops = ops;
    }

    public boolean isLeak() {
        return leak;
    }

    public void setLeak(boolean leak) {
        this.leak = leak;
    }
}
