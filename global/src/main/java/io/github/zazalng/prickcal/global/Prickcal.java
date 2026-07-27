package io.github.zazalng.prickcal.global;

import group.worldstandard.pudel.api.PluginContext;
import group.worldstandard.pudel.api.annotation.OnDisable;
import group.worldstandard.pudel.api.annotation.OnEnable;
import group.worldstandard.pudel.api.annotation.OnShutdown;
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
    private PluginContext ctx;

    // ==================== RUNTIME PREFIXED IDS (initialized in onEnable) ====================
    private String btnPrefix;
    private String modalPrefix;
    private String stringMenuPrefix;
    private String entityMenuPrefix;

    @OnEnable
    public void onEnable(PluginContext ctx) {
        this.ctx = ctx;
        // Initialization logic for runtime prefixed IDs
        String frontName = this.ctx.getDatabaseManager().getPluginId();
        this.btnPrefix = frontName + BTN_HANDLER;
        this.modalPrefix = frontName + MODAL_HANDLER;
        this.stringMenuPrefix = frontName + STRING_MENU_HANDLER;
        this.entityMenuPrefix = frontName + ENTITY_MENU_HANDLER;
    }

    @OnDisable
    public void onDisable(PluginContext ctx) {
        this.ctx = null;
    }

    @OnShutdown
    public void onShutdown(PluginContext ctx) {
        this.ctx = null;
    }
}