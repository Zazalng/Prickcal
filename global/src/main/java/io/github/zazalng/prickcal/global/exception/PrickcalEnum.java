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

public enum PrickcalEnum {
    INVALID_ACCOUNT(401, "Wait a minute, w-who are you!? Index of %s"),
    INVALID_APOSTLE(402, "Invalid Apostle Index of %s"),
    INVALID_CRAYONLINEUP(403, "Invalid Crayon Line Up Index of %s"),
    ARGS_EXCEPTION(100, "%s"),

    UNCATEGORY(-1, "Who da heck cause this exception without proper tell what cause error");

    private final int errCode;
    private final String errMsg;

    PrickcalEnum(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public static PrickcalEnum fromCode(int code) {
        for (PrickcalEnum ex : PrickcalEnum.values()) {
            if (ex.errCode == code) return ex;
        }

        return UNCATEGORY;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getErrMsg(String injector) {
        return errMsg.formatted(injector);
    }
}
