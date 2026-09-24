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
import io.github.zazalng.prickcal.global.contract.operator.Operator;
import io.github.zazalng.prickcal.global.exception.PrickcalEnum;
import io.github.zazalng.prickcal.global.exception.PrickcalException;

import java.time.Instant;

@Entity(tableName = "accounts")
public class Account {
    /**
     * Key record column require by API
     */
    @Column
    private Long id;
    /**
     * User's discord id number. <b>NOT SUPPOSED TO BE USED AS FOREIGN KEY.</b>
     */
    @Column(nullable = false, unique = true)
    private String uid;
    /**
     * User's in-game name
     */
    @Column
    private String ign;
    /**
     * User's in-game friend code
     */
    @Column
    private String friendCode;
    /**
     * User's operation level in {@link io.github.zazalng.prickcal.global.contract.operator.Operator}
     */
    @Column
    private short ops;
    /**
     * User's consult to seeing hidden/unrelease content of in-game data on plugin database
     */
    @Column(defaultValue = "false")
    private boolean leak;
    /**
     * User's contribution point
     */
    @Column(defaultValue = "0")
    private int cp;
    /**
     * A String of format that user using to let's Pudel auto-detect and recording to {@link CrayonRecord} when user using though context command on message in discord.
     * <ul>
     *     <li>%dd = date day (Integer valid with 0[1-9] or 1 to 31)</li>
     *     <li>%dm = date month (Integer valid with 0[1-9] or 1 to 12)</li>
     *     <li>%dy = date year (Integer valid with \d{2,4} for \d{2} added 2000 to it)</li>
     *     <li>%cs = candy spent (Integer valid only when mod by 20 and result is 0)</li>
     *     <li>%ca = crayon acquired (Integer valid with any >= 0)</li>
     * </ul>
     * Format %dd, %dm, %dy, %cs, %ca MUST EXIST to called it valid format
     */
    @Column(nullable = false, defaultValue = "%dd/%dm/%dy %cs %ca")
    private String crayonFormat;

    /**
     * A PDF template url (from discord.attachment)
     */
    @Column
    private String templateUrl;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIgn() {
        return ign;
    }

    public void setIgn(String ign) {
        this.ign = ign;
    }

    public short getOps() {
        return ops;
    }

    public void setOps(short ops) {
        this.ops = ops;
    }

    public boolean isLeak() {
        return leak;
    }

    public void setLeak(boolean leak) {
        this.leak = leak;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public String getCrayonFormat() {
        return crayonFormat;
    }

    public void setCrayonFormat(String crayonFormat) {
        this.crayonFormat = crayonFormat;
    }

    public String getFriendCode() {
        return friendCode;
    }

    public void setFriendCode(String friendCode) {
        this.friendCode = friendCode;
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

    public boolean isActionable(Operator level) {
        return ops <= level.getValue();
    }

    public String getDefaultText(String section) {
        return switch (section) {
            case "ign" -> getIgn();
            case "code" -> getFriendCode();
            case "format" -> getCrayonFormat();
            default ->
                    throw new PrickcalException(PrickcalEnum.ARGS_EXCEPTION, "Unexpect Argument on Account.getWish(%s)".formatted(section));
        };
    }

    /**
     * Validates that the given format string contains all required placeholders
     * (%dd, %dm, %dy, %cs, %ca) and does not contain the forbidden combinations
     * %cs%ca or %ca%cs. If the validation succeeds, the format is stored by
     * invoking {@link #setCrayonFormat(String)}.
     *
     * @param value the format string to validate
     */
    public void validateFormat(String value) {
        for (String token : new String[]{"%dd", "%dm", "%dy", "%cs", "%ca"}) {
            if (!value.contains(token)) {
                return;
            }
        }

        if (value.contains("%cs%ca") || value.contains("%ca%cs")) {
            return;
        }

        setCrayonFormat(value);
    }
}
