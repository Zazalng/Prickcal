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
import io.github.zazalng.prickcal.global.contract.trickcal.aposlte.StarUpAmount;

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
    private short init;
    /**
     * A maximum star that character can reach.
     */
    @Column
    private short max;
    /**
     * Belong to {@link CrayonLineUp}.id
     */
    @Column(nullable = false)
    private Long crayon;
    /**
     * Character's race number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleRace}
     */
    @Column
    private short race;
    /**
     * Character's personality (color) number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleColor}
     */
    @Column
    private short color;
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

    public short getInit() {
        return init;
    }

    public void setInit(short init) {
        this.init = init;
    }

    public short getMax() {
        return max;
    }

    public void setMax(short max) {
        this.max = max;
    }

    public Long getCrayon() {
        return crayon;
    }

    public void setCrayon(Long crayon) {
        this.crayon = crayon;
    }

    public short getRace() {
        return race;
    }

    public void setRace(short race) {
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

    public short getColor() {
        return color;
    }

    public void setColor(short color) {
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

    public short getPosition() {
        return position;
    }

    public void setPosition(short position) {
        this.position = position;
    }

    public String trueName() {
        return "%s %s".formatted(getName(), getElydn() == null ? ":star_of_david:" : ":six_pointed_star: %s".formatted(getElydn()));
    }

    public String missingPiece(ApostleTrack track) {
        int missingPiece = StarUpAmount.missingPiece(track.getCurrentStar(), getMax());
        if (missingPiece <= 0) {
            return "";
        } else {
            return " (Missing %d Pieces)".formatted(missingPiece);
        }
    }

    public String missingPiece() {
        int missingPiece = StarUpAmount.missingPiece(getInit(), getMax());
        return String.valueOf(missingPiece);
    }
}
