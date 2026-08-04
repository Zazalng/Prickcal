package io.github.zazalng.prickcal.global.contract.trickcal;

public enum GearType {
    PATK(1),
    MATK(2),
    PDEF(3),
    MDEF(4),
    CRIT(5),
    CRES(6),
    HP(7),
    UNKNOWN(0);

    private final int id;

    GearType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static GearType fromId(int id){
        for(GearType g:GearType.values()){
            if(g.id == id) return g;
        }
        return UNKNOWN;
    }
}
