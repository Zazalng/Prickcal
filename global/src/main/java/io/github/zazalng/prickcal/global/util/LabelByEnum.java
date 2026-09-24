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
package io.github.zazalng.prickcal.global.util;

import io.github.zazalng.prickcal.global.contract.trickcal.EnumInterface;
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroup;

public final class LabelByEnum {
    /**
     * Creates a {@code CheckboxGroup.Builder} populated with options derived from the supplied enum.
     *
     * @param prefix the string prefix used to generate the checkbox group's identifier
     * @param eClass the enum class whose constants must implement {@code EnumInterface}; each valid constant contributes an option label and value
     * @return a builder for a checkbox group containing the valid enum options
     */
    public static <E extends Enum<E> & EnumInterface> CheckboxGroup.Builder createCheckBoxGroup(String prefix, Class<E> eClass) {
        CheckboxGroup.Builder b = CheckboxGroup.create(prefix);
        for (E e : eClass.getEnumConstants()) {
            if (!e.isValid()) continue;
            b.addOption(e.getOptionLabel(), e.getOptionValue());
        }
        return b;
    }
}
