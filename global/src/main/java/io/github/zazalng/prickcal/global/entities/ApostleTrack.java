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
    private int star;

    @Column
    private Boolean[] crayon;
}
