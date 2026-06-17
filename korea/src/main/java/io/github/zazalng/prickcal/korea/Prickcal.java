package io.github.zazalng.prickcal.korea;

import group.worldstandard.pudel.api.annotation.Plugin;

@Plugin(
        name = "Prickcal [Global]",
        version = "0.0.1-indev",
        author = "Zazalng",
        description = "A plugin for personally tracking & collection Trickcal progression."
)
public class Prickcal {
    // ==================== HANDLER IDS (compile-time, used in annotations) ====================
    private static final String BTN_HANDLER = ":button:";
    private static final String MODAL_HANDLER = ":modal:";
    private static final String STRING_MENU_HANDLER = ":string:";
    private static final String ENTITY_MENU_HANDLER = ":entity:";

    // ==================== RUNTIME PREFIXED IDS (initialized in onEnable) ====================
    private String btnPrefix;
    private String modalPrefix;
    private String stringMenuPrefix;
    private String entityMenuPrefix;
}