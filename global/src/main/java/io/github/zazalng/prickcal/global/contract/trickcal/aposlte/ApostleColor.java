package io.github.zazalng.prickcal.global.contract.trickcal.aposlte;

import java.awt.*;
import java.util.Objects;

public enum ApostleColor {
    UNKNOWN(0, "Invalid", new Color(0, 0, 0)),
    GREEN(1, "Innocent", new Color(80, 210, 80)),
    TEAL(2, "Composed", new Color(0, 250, 255)),
    RED(3, "Mad", new Color(210, 0, 0)),
    YELLOW(4, "Vivacious", new Color(255, 255, 0)),
    PURPLE(5, "Depressed", new Color(155, 55, 255));


    private final int no;
    private final String personality;
    private final Color color;

    ApostleColor(int no, String personality, Color color) {
        this.no = no;
        this.personality = personality;
        this.color = color;
    }

    public static ApostleColor fromNo(int no) {
        for (ApostleColor a : ApostleColor.values()) {
            if (Objects.equals(no, a.getNo())) return a;
        }
        return UNKNOWN;
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
}
