package io.github.zazalng.prickcal.global.entities;

import group.worldstandard.pudel.api.database.Column;
import group.worldstandard.pudel.api.database.Entity;

@Entity
public class CrayonLineUp {
    @Column
    private Long id;
    @Column
    private int house1A;
    @Column
    private int house1B;
    @Column
    private int house2A;
    @Column
    private int house2B;
    @Column
    private int house2C;
    @Column
    private int house3A;
    @Column
    private int house3B;
    @Column
    private int house3C;
    @Column
    private int house3D;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getHouse1A() {
        return house1A;
    }

    public void setHouse1A(int house1A) {
        this.house1A = house1A;
    }

    public int getHouse1B() {
        return house1B;
    }

    public void setHouse1B(int house1B) {
        this.house1B = house1B;
    }

    public int getHouse2A() {
        return house2A;
    }

    public void setHouse2A(int house2A) {
        this.house2A = house2A;
    }

    public int getHouse2B() {
        return house2B;
    }

    public void setHouse2B(int house2B) {
        this.house2B = house2B;
    }

    public int getHouse2C() {
        return house2C;
    }

    public void setHouse2C(int house2C) {
        this.house2C = house2C;
    }

    public int getHouse3A() {
        return house3A;
    }

    public void setHouse3A(int house3A) {
        this.house3A = house3A;
    }

    public int getHouse3B() {
        return house3B;
    }

    public void setHouse3B(int house3B) {
        this.house3B = house3B;
    }

    public int getHouse3C() {
        return house3C;
    }

    public void setHouse3C(int house3C) {
        this.house3C = house3C;
    }

    public int getHouse3D() {
        return house3D;
    }

    public void setHouse3D(int house3D) {
        this.house3D = house3D;
    }
}
