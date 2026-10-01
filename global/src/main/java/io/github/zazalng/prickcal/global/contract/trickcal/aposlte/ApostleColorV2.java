package io.github.zazalng.prickcal.global.contract.trickcal.aposlte;

import io.github.zazalng.prickcal.global.contract.EnumInterface;

import java.awt.*;

/**
 * The second-generation personality color of an apostle, keyed by a bitmask number rather than
 * a single index, so two personalities can be combined in one value.
 */
public enum ApostleColorV2 implements EnumInterface {
    /**
     * Placeholder for the empty bitmask, drawn in black.
     */
    Invalid(0b00000, "Invalid", new Color(0, 0, 0), false),
    /**
     * Innocent personality, bitmask 10000, drawn in green.
     */
    GREEN(0b10000, "Innocent", new Color(80, 210, 80)),
    /** Composed personality, bitmask 01000, drawn in cyan. */
    TEAL(0b01000, "Composed", new Color(0, 250, 255)),
    /** Madness personality, bitmask 00100, drawn in red. */
    RED(0b00100, "Madness", new Color(210, 0, 0)),
    /** Vivacious personality, bitmask 00010, drawn in yellow. */
    YELLOW(0b00010, "Vivacious", new Color(255, 255, 0)),
    /** Depressed personality, bitmask 00001, drawn in purple. */
    PURPLE(0b00001, "Depressed", new Color(155, 55, 255)),
    /** Every personality bit set at once, mask 11111, drawn in white. */
    RAINBOW(0b11111, "Rainbow", new Color(255, 255, 255));

    /** The personality bitmask number. */
    private final int no;
    /** The display name of the personality. */
    private final String personality;
    /** The RGB color the personality is drawn with. */
    private final Color color;
    /** Whether {@link #no} is a real personality mask. */
    private final boolean valid;

    ApostleColorV2(int no, String personality, Color color, boolean valid) {
        this.no = no;
        this.personality = personality;
        this.color = color;
        this.valid = valid;
    }

    ApostleColorV2(int no, String personality, Color color) {
        this(no, personality, color, true);
    }

    /**
     * The bitmask identifying this personality.
     *
     * @return the mask number, from a single bit up to {@code 0b11111}
     */
    public int getNo() {
        return no;
    }

    /**
     * The display name of the personality, also used as the select-menu label.
     *
     * @return the personality name, for example {@code "Innocent"}
     */
    public String getPersonality() {
        return personality;
    }

    /**
     * The RGB color this personality is drawn with.
     *
     * @return the display color; black for {@link #Invalid}
     */
    public Color getColor() {
        return color;
    }

    /** {@inheritDoc} */
    @Override
    public String getOptionLabel() {
        return personality;
    }

    /**
     * The personality bitmask rendered as its decimal text for the Discord select menu.
     *
     * @return the mask number as a String
     */
    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }

    /**
     * Whether this constant is a real personality rather than the empty-mask placeholder.
     *
     * @return {@code false} only for {@link #Invalid}
     */
    @Override
    public boolean isValid() {
        return valid;
    }
}
