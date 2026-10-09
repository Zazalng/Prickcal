package io.github.zazalng.prickcal.global.contract.template;

public enum UserPlaceholder {
    //BASIC
    IGN("ign"),
    /**
     * Specifies the format used to represent the consent date.
     * <p>
     * The value may be empty, in which case the default format is used.
     * Otherwise, it must follow the format {@code %f:%l-%c} case-insensitive,
     * <p>
     * where:
     * <ul>
     * <li>{@code %f} is a single character ({@code S}, {@code F}, {@code D}, or {@code R}),
     * representing Short Date, Full Date, Date, or Relative Time.</li>
     * <li>{@code %l} is a two-letter ISO 639-1 language code.</li>
     * <li>{@code %c} is a two-letter ISO 3166-1 or three-letter ISO 3166-(2,3) country code.</li>
     * </ul>
     * <p>
     * If the value does not conform to this format, it is treated as empty and
     * the default format is used as {@code "r:en-US"}.
     */
    CONSENT("consent_date", "s:en-US"),
    FRIENDCODE("friend_code"),
    APOSTLEOWN("apostle_own"),
    APOSTLEMAX("apostle_max"),
    LASTUPDATE("last_update"),
    CONTRIBUTION("contribution"),
    //CRAYON RECORD
    CANDYSPEND("candy_spend"),
    CRAYONGET("crayon_acquired"),
    CRAYONRATE("crayon_rate"),
    /**
     * Specifies the format used to represent the first record date.
     * <p>
     * The value may be empty, in which case the default format is used.
     * Otherwise, it must follow the format {@code %f:%l-%c} case-insensitive,
     * <p>
     * where:
     * <ul>
     * <li>{@code %f} is a single character ({@code S}, {@code F}, {@code D}, or {@code R}),
     * representing Short Date, Full Date, Date, or Relative Time.</li>
     * <li>{@code %l} is a two-letter ISO 639-1 language code.</li>
     * <li>{@code %c} is a two-letter ISO 3166-1 or three-letter ISO 3166-(2,3) country code.</li>
     * </ul>
     * <p>
     * If the value does not conform to this format, it is treated as empty and
     * the default format is used as {@code "r:en-US"}.
     */
    FIRSTRECORD("first_record_date", "r:en-US"),

    //CRAYON GRID
    ATKGET("get_atk"),
    ATKMAX("max_atk"),
    MHPGET("get_hp"),
    MHPMAX("max_hp"),
    CRTGET("get_crit"),
    CRTMAX("max_crit"),
    DEFGET("get_def"),
    DEFMAX("max_def"),
    CRSGET("get_cres"),
    CRSMAX("max_cres"),
    //CRAYON SPENT
    CRAYONSPENT("crayon_spent"),
    CRAYONMAX("crayon_possible");

    private final String fieldName;
    private final String fieldValue;

    UserPlaceholder(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    UserPlaceholder(String fieldName) {
        this(fieldName, null);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
