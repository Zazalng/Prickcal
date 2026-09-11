package io.github.zazalng.prickcal.global.manager;

public interface Manager {
    <T extends Manager> T initialize();

    void reload();

    void shutdown();
}
