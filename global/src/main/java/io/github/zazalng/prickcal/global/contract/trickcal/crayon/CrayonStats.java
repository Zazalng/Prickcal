package io.github.zazalng.prickcal.global.contract.trickcal.crayon;

public enum CrayonStats {
    ATK(1,"ATK"),
    HP(2,"HP"),
    DEF(3,"DEF"),
    CRIT(4,"Crit Rate"),
    CRES(5,"Crit Resistance"),
    UNKNOWN(0, "Unknown");

    private final int no;
    private final String name;

    CrayonStats(int no, String name) {
        this.no = no;
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public static CrayonStats fromNo(int no) {
        for (CrayonStats stat : CrayonStats.values()) {
            if (stat.getNo() == no) {
                return stat;
            }
        }
        return UNKNOWN;
    }
}
