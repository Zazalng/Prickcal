package io.github.zazalng.prickcal.global.manager;

public enum ManagersEnum {
    ACCOUNT(AccountManager.class),
    APOSTLE(AbstractManager.class),
    SESSION(SessionManager.class);

    private final Class<? extends Manager> managerClass;

    <T extends Manager> ManagersEnum(Class<T> managerClass) {
        this.managerClass = managerClass;
    }

    @SuppressWarnings("unchecked")
    public <T extends Manager> Class<T> getManagerClass() {
        return (Class<T>) this.managerClass;
    }
}
