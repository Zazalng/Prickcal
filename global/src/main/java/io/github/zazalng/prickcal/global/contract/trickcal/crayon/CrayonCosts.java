package io.github.zazalng.prickcal.global.contract.trickcal.crayon;

public enum CrayonCosts {
    HOUSE_1(1, 2, 2),
    HOUSE_2(2, 4, 3),
    HOUSE_3(3, 6, 4),
    UNKNOWN(0, -1, -1);

    private final int level;
    private final int price;
    private final int amount;

    CrayonCosts(int level, int price, int amount){
        this.level = level;
        this.price = price;
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public static CrayonCosts fromLevel(int level){
        for(CrayonCosts e: CrayonCosts.values()){
            if(e.level == level) return e;
        }

        return UNKNOWN;
    }
}
