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
package io.github.zazalng.prickcal.global.contract;

/**
 * Shared contract of the game-data enums that can be rendered as a Discord select-menu option,
 * so any of them can be offered to the user without knowing its concrete type.
 */
public interface DiscordEnumLabelInterface {
    /**
     * The text shown to the user as the option name.
     *
     * @return a human-readable label for this constant
     */
    String getOptionLabel();

    /**
     * The value submitted back by Discord when the user picks this option.
     *
     * @return a string form of the game-data value that the matching lookup accepts
     */
    String getOptionValue();

    /**
     * Whether this constant represents a real game value rather than a placeholder.
     *
     * @return {@code true} when the constant maps to actual game data
     */
    boolean isValid();
}
