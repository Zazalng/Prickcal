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
package io.github.zazalng.prickcal.global.exception;

/**
 * The catalogue of error conditions the plugin raises, each pairing a numeric code with a message template.
 * <p>
 * Every template takes exactly one {@code %s} placeholder for the offending identifier, supplied by the
 * caller through {@link #getErrMsg(String)}. {@link #fromCode(int)} falls back to {@link #UNCATEGORY}, so every
 * integer maps to a constant.
 */
public enum PrickcalEnum {
    /**
     * Raised when an account lookup resolves to no record.
     */
    INVALID_ACCOUNT(401, "Wait a minute, w-who are you!? Index of %s"),
    /** Raised when an apostle id or index has no matching record. */
    INVALID_APOSTLE(402, "Invalid Apostle Index of %s"),
    /** Raised when a referenced crayon line-up id or index has no matching record. */
    INVALID_CRAYONLINEUP(403, "Invalid Crayon Line Up Index of %s"),
    /**
     * Raised when a caller passes an argument this plugin does not recognize or support.
     */
    ARGS_EXCEPTION(100, "%s"),

    /** The catch-all for an exception raised without a categorized error condition. */
    UNCATEGORY(-1, "Who da heck cause this exception without proper tell what cause error");

    private final int errCode;
    private final String errMsg;

    /**
     * Creates a catalogue entry.
     *
     * @param errCode the numeric code that uniquely identifies the error condition
     * @param errMsg  the message template, containing one {@code %s} placeholder for the caller's value
     */
    PrickcalEnum(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    /**
     * Resolves the error condition with the given code.
     *
     * @param code the numeric error code to look up
     * @return the matching constant, or {@link #UNCATEGORY} when no constant carries that code
     */
    public static PrickcalEnum fromCode(int code) {
        for (PrickcalEnum ex : PrickcalEnum.values()) {
            if (ex.errCode == code) return ex;
        }

        return UNCATEGORY;
    }

    /**
     * Returns the numeric code of this error condition.
     *
     * @return the error code, used by callers that need to branch on the failure without parsing the message
     */
    public int getErrCode() {
        return errCode;
    }

    /**
     * Renders this error condition's message with the caller's value substituted in.
     *
     * @param injector the value substituted into the template's single {@code %s} placeholder
     * @return the formatted message, for example {@code Invalid Apostle Index of 42}
     */
    public String getErrMsg(String injector) {
        return errMsg.formatted(injector);
    }
}
