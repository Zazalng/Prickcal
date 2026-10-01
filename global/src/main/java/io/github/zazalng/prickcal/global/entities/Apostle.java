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

    /**
     * Key record column require by API (as well as using for tracking via apostle_id from other table).
     */
    public Long getId() {
        return id;
    }

    /**
     * Key record column require by API (as well as using for tracking via apostle_id from other table).
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** Character's name. */
    public String getName() {
        return name;
    }

    /** Character's name. */
    public void setName(String name) {
        this.name = name;
    }

    /** Character's in-game image (url). */
    public String getPic() {
        return pic;
    }

    /** Character's in-game image (url). */
    public void setPic(String pic) {
        this.pic = pic;
    }

    /** An initialize star that character gain. */
    public short getInit() {
        return init;
    }

    /** An initialize star that character gain. */
    public void setInit(short init) {
        this.init = init;
    }

    /** A maximum star that character can reach. */
    public short getMax() {
        return max;
    }

    /** A maximum star that character can reach. */
    public void setMax(short max) {
        this.max = max;
    }

    /** Belong to {@link CrayonLineUp}.id */
    public Long getCrayon() {
        return crayon;
    }

    /** Belong to {@link CrayonLineUp}.id */
    public void setCrayon(Long crayon) {
        this.crayon = crayon;
    }

    /** Character's race number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleRace} */
    public short getRace() {
        return race;
    }

    /** Character's race number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleRace} */
    public void setRace(short race) {
        this.race = race;
    }

    /** Is character had elydn title? */
    public String getElydn() {
        return elydn;
    }

    /** Is character had elydn title? */
    public void setElydn(String elydn) {
        this.elydn = elydn;
    }

    /**
     * Whether the elydn title is stored as a present but empty value.
     */
    public boolean isItElydn() {
        return getElydn() != null && !getElydn().isEmpty();
    }

    /** A String in Array format of {@link Hashtag}.id that this character had. */
    public String getHashtag() {
        return hashtag;
    }

    /** A String in Array format of {@link Hashtag}.id that this character had. */
    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    /** Character's personality (color) number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleColor} */
    public short getColor() {
        return color;
    }

    /** Character's personality (color) number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostleColor} */
    public void setColor(short color) {
        this.color = color;
    }

    /** Instant of record creation. */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** Instant of record creation. */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /** Instant of last update. */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /** Instant of last update. */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** Character's position number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostlePosition} */
    public short getPosition() {
        return position;
    }

    /** Character's position number that will correction with {@link io.github.zazalng.prickcal.global.contract.trickcal.aposlte.ApostlePosition} */
    public void setPosition(short position) {
        this.position = position;
    }

    /**
     * Compose a display name of this character by combining its name with the elydn title.
     * A present {@link #getElydn()} is rendered as {@code ":six_pointed_star: <elydn>"}, otherwise a
     * plain {@code ":star_of_david:"} is appended.
     *
     * @return display name of this character with the elydn title
     */
    public String trueName() {
        return "%s %s".formatted(getName(), getElydn() == null ? ":star_of_david:" : ":six_pointed_star: %s".formatted(getElydn()));
    }

    /**
     * Count the certificate pieces that the given track still lack to reach {@link #getMax()}.
     *
     * @param track the track of this character to count the missing pieces from its current star
     * @return an empty string when nothing is missing, otherwise {@code " (Missing <n> Pieces)"}
     */
    public String missingPiece(ApostleTrack track) {
        int missingPiece = StarUpAmount.missingPiece(track.getCurrentStar(), getMax());
        if (missingPiece <= 0) {
            return "";
        } else {
            return " (Missing %d Pieces)".formatted(missingPiece);
        }
    }

    /**
     * Count the certificate pieces needed to raise this character from {@link #getInit()} to {@link #getMax()}.
     *
     * @return the missing piece count as a decimal String
     */
    public String missingPiece() {
        int missingPiece = StarUpAmount.missingPiece(getInit(), getMax());
        return String.valueOf(missingPiece);
    }
}
