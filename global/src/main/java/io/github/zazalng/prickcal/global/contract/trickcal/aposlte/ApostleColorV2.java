package io.github.zazalng.prickcal.global.contract.trickcal.aposlte;

import io.github.zazalng.prickcal.global.contract.DiscordEnumLabelInterface;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The second-generation personality color of an apostle, keyed by a bitmask number rather than
 * a single index, so two personalities can be combined in one value.
 */
public enum ApostleColorV2 implements DiscordEnumLabelInterface {
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

    /**
     * Resolve the personality color of an apostle from its stored color number.
     * The search covers every declared constant, so the number -1 resolves to {@link #Invalid}
     * itself; any other unmatched number, including the commented-out dual combinations, also
     * falls back to {@link #Invalid}.
     *
     * @param no the color number read from an apostle row
     * @return the matching constant, or {@link #Invalid} when no constant carries that number
     */
    public static ApostleColorV2 fromNo(short no) {
        if (no < 0) return Invalid;
        int val = no & 0xFF;
        if (val == 0) return Invalid;
        if (val == 0b11111) return RAINBOW;

        for (ApostleColorV2 a : ApostleColorV2.values()) {
            if (a != Invalid && a != RAINBOW && val == a.no) return a;
        }
        return Invalid;
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

    /**
     * Resolve and combine personality names based on the bitmask.
     *
     * @param no the color number read from an apostle row
     * @return the combined personality names, e.g., \"Innocent / Depressed\"
     */
    public static String getCombinedPersonality(short no) {
        if (no < 0) return Invalid.getPersonality();
        int val = no & 0xFF;
        if (val == 0) return Invalid.getPersonality();
        if (val == 0b11111) return RAINBOW.getPersonality();

        List<String> parts = new ArrayList<>();
        for (ApostleColorV2 a : ApostleColorV2.values()) {
            if (a != Invalid && a != RAINBOW && (val & a.no) != 0) {
                parts.add(a.getPersonality());
            }
        }
        return parts.isEmpty() ? Invalid.getPersonality() : String.join(" / ", parts);
    }
}