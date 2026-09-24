package io.github.zazalng.prickcal.global.contract.entity;

import io.github.zazalng.prickcal.global.contract.trickcal.EnumInterface;

public enum TableEntity implements EnumInterface {
    ACCOUNT("accounts", false),
    APOSTLE_REVIEW("apostle_reviews"),
    APOSTLE_TRACK("apostle_tracks", false),
    APOSTLE("apostles"),
    CRAYON_LINE_UP("crayon_line_ups"),
    CRAYON_RECORD("crayon_records", false),
    GIFT_ACQUIRED("gift_acquired", false),
    GIFT_CODE("gift_codes"),
    HASH_TAG("hash_tags"),
    LOG("logs"),
    REMARKABLE_RECORD("remarkable_records", false),
    STAGE_GEAR_DROP("stage_gear_drops");

    private final String tableName;
    private final boolean pubState;

    TableEntity(String tableName, boolean pubState) {
        this.tableName = tableName;
        this.pubState = pubState;
    }

    TableEntity(String tableName) {
        this(tableName, true);
    }

    @Override
    public String getOptionLabel() {
        return name();
    }

    @Override
    public String getOptionValue() {
        return tableName;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
