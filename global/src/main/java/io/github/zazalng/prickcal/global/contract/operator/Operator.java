package io.github.zazalng.prickcal.global.contract.operator;

public enum Operator {
    ADMIN(0),
    EDITOR(1),
    USER(2),
    UNKNOWN(-1);

    private final int value;

    Operator(int value) {
        this.value = value;
    }

    public static Operator fromString(int operator) {
        for (Operator o : Operator.values()) {
            if (o.value == operator) {
                return o;
            }
        }
        return UNKNOWN;
    }
}
