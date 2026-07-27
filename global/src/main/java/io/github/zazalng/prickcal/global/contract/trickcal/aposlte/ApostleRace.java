package io.github.zazalng.prickcal.global.contract.trickcal.aposlte;

public enum ApostleRace {
    UNKNOWN(0, "Unknown"),
    SPRITE(1, "Sprite"),
    ELEMENTAL(2, "Elemental"),
    BEASTMEN(3, "Beastmen"),
    DRAGON(4, "Dragon"),
    PHANTOM(5, "Phantom"),
    ELF(6, "Elf"),
    WITCH(7, "Witch"),
    MYSTIC(8, "Mystic");

    private final int no;
    private final String name;

    ApostleRace(int no, String name) {
        this.no = no;
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public static ApostleRace fromNo(int no) {
        for (ApostleRace race : ApostleRace.values()) {
            if (race.getNo() == no) {
                return race;
            }
        }
        return UNKNOWN;
    }
}
