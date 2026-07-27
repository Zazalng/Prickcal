package io.github.zazalng.prickcal.global.contract.trickcal.crayon;

public enum CrayonCosts {
    HOUSE_1(2, 2),
    HOUSE_2(4, 3),
    HOUSE_3(6, 4);

    private final int price;
    private final int amount;

    CrayonCosts(int price, int amount){
        this.price = price;
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }
}
