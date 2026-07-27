package io.github.zazalng.prickcal.global.contract.trickcal.aposlte;

import io.github.zazalng.prickcal.global.entities.Apostle;

public enum StarUpAmount{
    S1(25),
    S2(12),
    S3(20),
    S4(25),
    S5(50),
    S6(0),
    S7(40),
    S8(60);

    private final int piece;

    StarUpAmount(int piece){
        this.piece = piece;
    }

    public int getAmount(Apostle ch){
        int piece = StarUpAmount.valueOf("S".concat(String.valueOf(ch.getInit()))).piece;
    }
}
