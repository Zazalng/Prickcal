package io.github.zazalng.prickcal.global.entities;

import group.worldstandard.pudel.api.database.Column;
import group.worldstandard.pudel.api.database.Entity;

@Entity
public class Apostle {
    @Column
    private Long id;
    @Column
    private String name;
    @Column
    private String pic;
    @Column
    private int init;
    @Column
    private Long crayon;
    @Column
    private int race;
    @Column
    private boolean elydn;
    @Column
    private Long[] hashtag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getInit() {
        return init;
    }

    public void setInit(int init) {
        this.init = init;
    }

    public Long getCrayon() {
        return crayon;
    }

    public void setCrayon(Long crayon) {
        this.crayon = crayon;
    }

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = race;
    }

    public boolean isElydn() {
        return elydn;
    }

    public void setElydn(boolean elydn) {
        this.elydn = elydn;
    }

    public Long[] getHashtag() {
        return hashtag;
    }

    public void setHashtag(Long[] hashtag) {
        this.hashtag = hashtag;
    }
}
