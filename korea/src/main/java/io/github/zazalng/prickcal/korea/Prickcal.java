package io.github.zazalng.prickcal.korea;

import group.worldstandard.pudel.api.annotation.Plugin;

/**
 * The Korea-flavoured entry point of the Prickcal plugin.
 * <p>
 * Holds the handler ID constants used in the interaction annotations of this module, plus the
 * schema-scoped runtime prefixes that are meant to be derived from them once the plugin is enabled. The
 * prefixes are declared but never assigned in this module: the control panel, command and event handling
 * that would use them currently live in the global module, which carries its own prefixes.
 * <p>
 * No lifecycle callback is implemented here yet, so the module registers no listeners of its own.
 */
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