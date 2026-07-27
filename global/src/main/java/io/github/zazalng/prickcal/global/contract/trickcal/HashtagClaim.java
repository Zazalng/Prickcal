package io.github.zazalng.prickcal.global.contract.trickcal;

public enum HashtagClaim {
    NEGATIVE,
    NATURE,
    POSITIVE;

    public static HashtagClaim fromValue(int value) {
        if(value > 0){
            return POSITIVE;
        } else if(value < 0){
            return NEGATIVE;
        } else {
            return NATURE;
        }
    }
}
