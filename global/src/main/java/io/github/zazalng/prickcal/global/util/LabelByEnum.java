package io.github.zazalng.prickcal.global.util;

import io.github.zazalng.prickcal.global.contract.trickcal.EnumInterface;
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroup;

public class LabelByEnum {
    public static <E extends Enum<E> & EnumInterface> CheckboxGroup.Builder createCheckBoxGroup(String prefix, Class<E> eClass) {
        CheckboxGroup.Builder b = CheckboxGroup.create(prefix);
        for (E e : eClass.getEnumConstants()) {
            b.addOption(e.getOptionLabel(), e.getOptionValue());
        }
        return b;
    }
}
