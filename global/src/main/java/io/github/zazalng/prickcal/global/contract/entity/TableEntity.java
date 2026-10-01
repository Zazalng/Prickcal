package io.github.zazalng.prickcal.global.contract.entity;

import io.github.zazalng.prickcal.global.contract.EnumInterface;

/**
 * Every database table the plugin tracks, keyed by its physical table name. The constants marked
 * with a {@code false} publication state are the per-user tables.
 */
public enum TableEntity implements EnumInterface {
    /**
     * The account table, holding one row per user.
     */
    ACCOUNT("accounts", false),
    /**
     * The apostle review table.
     */
    APOSTLE_REVIEW("apostle_reviews"),
    /** The apostle track table, holding one row per user per apostle. */
    APOSTLE_TRACK("apostle_tracks", false),
    /** The apostle table, the game-data characters. */
    APOSTLE("apostles"),
    /** The crayon line-up table, the stat line-up of each apostle. */
    CRAYON_LINE_UP("crayon_line_ups"),
    /** The crayon record table, holding one row per user per crayon acquisition. */
    CRAYON_RECORD("crayon_records", false),
    /** The gift acquired table, holding one row per user per redeemed gift code. */
    GIFT_ACQUIRED("gift_acquired", false),
    /** The gift code table. */
    GIFT_CODE("gift_codes"),
    /** The hashtag table. */
    HASH_TAG("hash_tags"),
    /** The audit log table. */
    LOG("logs"),
    /** The remarkable record table, holding one row per user per remarkable draw. */
    REMARKABLE_RECORD("remarkable_records", false),
    /** The stage gear drop table. */
    STAGE_GEAR_DROP("stage_gear_drops");

    /** The physical table name this constant stands for. */
    private final String tableName;
    /** Whether the table is published rather than scoped to a single user. */
    private final boolean pubState;

    TableEntity(String tableName, boolean pubState) {
        this.tableName = tableName;
        this.pubState = pubState;
    }

    TableEntity(String tableName) {
        this(tableName, true);
    }

    /**
     * The constant name such as {@code "APOSTLE_TRACK"}, used as its select-menu label.
     *
     * @return the enum constant name
     */
    @Override
    public String getOptionLabel() {
        return name();
    }

    /**
     * The physical table name, rendered as the value of a Discord select-menu option.
     *
     * @return the table name, for example {@code "apostle_tracks"}
     */
    @Override
    public String getOptionValue() {
        return tableName;
    }

    /**
     * Whether this constant names a tracked table. The method returns {@code true} for every
     * constant and does not consult the publication state, which is stored but not exposed here.
     *
     * @return {@code true} for every constant
     */
    @Override
    public boolean isValid() {
        return true;
    }
}
