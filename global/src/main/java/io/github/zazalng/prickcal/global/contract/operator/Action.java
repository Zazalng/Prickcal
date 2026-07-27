package io.github.zazalng.prickcal.global.contract.operator;

public enum Action {
    CREATE,
    UPDATE,
    DELETE,
    UNKNOWN;

    public static Action fromString(String action) {
        for (Action a : Action.values()) {
            if (a.name().equalsIgnoreCase(action)) {
                return a;
            }
        }
        return UNKNOWN;
    }
}
