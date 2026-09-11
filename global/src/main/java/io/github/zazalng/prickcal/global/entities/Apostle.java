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
package io.github.zazalng.prickcal.global.entities;

import group.worldstandard.pudel.api.database.Column;
import group.worldstandard.pudel.api.database.Entity;

import java.time.Instant;

@Entity(tableName = "apostles")
public class Apostle {
    /**
     * Key record column require by API (as well as using for tracking via apostle_id from other table)
     */
    @Column
    private Long id;
    /**
     * Character's name.
     */
    @Column(nullable = false, unique = true)
    private String name;
    /**
     * Character's in-game image (url).
     */
    @Column
    private String pic;
    /**
     * An initialize star that character gain.
     */
    @Column
    private int init;
    /**
     * A maximum star that character can reach.
     */
    @Column
    private int max;
    /**
     * Belong to {@link CrayonLineUp}.id
     */
    @Column(nullable = false)
    private Long crayon;
    /**
     * Character's race number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleRace}
     */
    @Column
    private int race;
    /**
     * Character's personality (color) number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleColor}
     */
    @Column
    private int color;
    /**
     * Is character had elydn title?
     */
    @Column
    private String elydn;
    /**
     * Character's position number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostlePosition}
     */
    @Column(defaultValue = "-1")
    private short position;
    /**
     * A String in Array format of {@link Hashtag}.id that this character had.
     */
    @Column(nullable = false)
    private String hashtag;
    /**
     * A release date of character use for check before or after now() to mark character for leak.
     */
    @Column(nullable = false)
    private Instant releaseDate;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

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

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
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

    public String getElydn() {
        return elydn;
    }

    public boolean isElydn() {
        return getElydn() != null && getElydn().isEmpty();
    }

    public void setElydn(String elydn) {
        this.elydn = elydn;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
