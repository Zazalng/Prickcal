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
package io.github.zazalng.prickcal.global.contract.operator;

import io.github.zazalng.prickcal.global.contract.DiscordEnumLabelInterface;

/**
 * Represents the available PDF templates used for generating documents.
 * Implements {@link DiscordEnumLabelInterface} to allow rendering as selectable options
 * in a user interface.
 */
public enum PdfTemplate implements DiscordEnumLabelInterface {
    PROFILE("Profile Template"),
    APOSTLE("Apostle Template"),
    INVALID("INVALID", false);

    private final String label;
    private final boolean valid;

    PdfTemplate(String label, boolean valid) {
        this.label = label;
        this.valid = valid;
    }

    PdfTemplate(String label) {
        this(label, true);
    }

    @Override
    public String getOptionLabel() {
        return label;
    }

    @Override
    public String getOptionValue() {
        return name().toLowerCase();
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
