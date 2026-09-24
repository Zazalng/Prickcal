package io.github.zazalng.prickcal.global.contract.trickcal.aposlte;

import io.github.zazalng.prickcal.global.contract.trickcal.EnumInterface;

import java.awt.*;

public enum ApostleColorV2 implements EnumInterface {
    Invalid(0b00000, "Invalid", new Color(0, 0, 0), false),
    GREEN(0b10000, "Innocent", new Color(80, 210, 80)),
    TEAL(0b01000, "Composed", new Color(0, 250, 255)),
    RED(0b00100, "Madness", new Color(210, 0, 0)),
    YELLOW(0b00010, "Vivacious", new Color(255, 255, 0)),
    PURPLE(0b00001, "Depressed", new Color(155, 55, 255)),
    RAINBOW(0b11111, "Rainbow", new Color(255, 255, 255));

    private final int no;
    private final String personality;
    private final Color color;
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

    public int getNo() {
        return no;
    }

    public String getPersonality() {
        return personality;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String getOptionLabel() {
        return personality;
    }

    @Override
    public String getOptionValue() {
        return String.valueOf(no);
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}
