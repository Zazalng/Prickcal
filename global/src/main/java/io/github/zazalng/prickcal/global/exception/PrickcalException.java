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

public class PrickcalException extends RuntimeException {
    private final PrickcalEnum exceptionType;

    /**
     * Creates an exception with a known error type and a value to inject into its message.
     *
     * @param title    the error condition, whose message template is {@link PrickcalEnum#getErrMsg(String)}-formatted
     * @param injector the value substituted into the message template's single {@code %s} placeholder
     */
    public PrickcalException(PrickcalEnum title, String injector) {
        exceptionType = title;
        super(title.getErrMsg(injector));
    }

    /**
     * Creates an exception from a raw error code, resolving the type via {@link PrickcalEnum#fromCode(int)}.
     * <p>
     * An unrecognised code resolves to {@link PrickcalEnum#UNCATEGORY}, so the resulting error code is the
     * category's rather than the one supplied.
     *
     * @param code     the numeric error code to resolve into a {@link PrickcalEnum}
     * @param injector the value substituted into the message template's single {@code %s} placeholder
     */
    public PrickcalException(int code, String injector) {
        PrickcalEnum exceptionType = PrickcalEnum.fromCode(code);
        super(exceptionType.getErrMsg(injector));
        this.exceptionType = exceptionType;
    }

    /**
     * Returns the numeric code of the resolved error type.
     *
     * @return the error code, which is {@link PrickcalEnum#UNCATEGORY}'s when the type was resolved from an unknown code
     */
    public int getErrorCode(){
        return exceptionType.getErrCode();
    }
}
