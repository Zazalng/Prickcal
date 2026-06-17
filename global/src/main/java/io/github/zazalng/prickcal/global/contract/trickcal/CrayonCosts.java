package io.github.zazalng.prickcal.global.contract.trickcal;

public enum CrayonCosts {
    H1(2, 2),
    H2(4, 3),
    H3(6, 4);

    private final int price;
    private final int amount;

    CrayonCosts(int price, int amount){
        this.price = price;
        this.amount = amount;
    }
}
