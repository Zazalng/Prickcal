package io.github.zazalng.prickcal.global.contract.trickcal.aposlte;

import java.util.Objects;

public enum ApostlePosition {
    FRONT(1, "Front Column"),
    MID(2, "Mid Column"),
    BACK(3, "Back Column"),
    ROBIN(0, "Round Robin"),
    UNKNOW(-1, "Invalid");

    private final int no;
    private final String seat;


    ApostlePosition(int no, String seat) {
        this.no = no;
        this.seat = seat;
    }

    public static ApostlePosition fromNo(int no) {
        for (ApostlePosition a : ApostlePosition.values()) {
            if (Objects.equals(no, a.getNo())) return a;
        }
        return UNKNOW;
    }

    public int getNo() {
        return no;
    }

    public String getSeat() {
        return seat;
    }
}
