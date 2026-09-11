package io.github.zazalng.prickcal.global.contract.trickcal.crayon;

public enum CrayonCosts {
    HOUSE_1(1, 2, 3),
    HOUSE_2(2, 4, 4),
    HOUSE_3(3, 6, 5),
    UNKNOWN(0, -1, -1);

    private final int depth;
    private final int price;
    private final int amount;

    CrayonCosts(int depth, int price, int amount) {
        this.depth = depth;
        this.price = price;
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public static CrayonCosts fromDepth(int depth) {
        for(CrayonCosts e: CrayonCosts.values()){
            if (e.depth == depth) return e;
        }

        return UNKNOWN;
    }
}
