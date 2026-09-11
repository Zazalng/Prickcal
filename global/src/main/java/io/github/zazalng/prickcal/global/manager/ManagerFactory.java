package io.github.zazalng.prickcal.global.manager;

import group.worldstandard.pudel.api.PluginContext;

import java.util.EnumMap;

public class ManagerFactory {
    private final PluginContext ctx;
    private final RepositoryProvider repos;
    private final EnumMap<ManagersEnum, Manager> managers = new EnumMap<>(ManagersEnum.class);

    public ManagerFactory(PluginContext ctx, RepositoryProvider repos) {
        this.ctx = ctx;
        this.repos = repos;
        startAllManagers();
    }

    public PluginContext getCtx() {
        return ctx;
    }

    public RepositoryProvider getRepos() {
        return repos;
    }

    public <T extends Manager> T getManager(ManagersEnum managerName) {
        Manager manager = this.managers.get(managerName);
        Class<T> expectedClass = managerName.getManagerClass();

        if (expectedClass.isInstance(manager)) {
            return expectedClass.cast(manager);
        }

        throw new IllegalArgumentException(
                "Manager with name " + managerName + " is not of type " + expectedClass.getName()
        );
    }

    private ManagerFactory loadManager(ManagersEnum managerName, Manager manager) {
        this.managers.put(managerName, manager);
        return this;
    }

    public ManagerFactory reloadManager(ManagersEnum managerName) {
        Manager manager = this.managers.get(managerName);
        if (manager != null) {
            manager.reload();
        }
        return this;
    }

    public ManagerFactory shutdownAllManagers() {
        this.managers.values().forEach(Manager::shutdown);
        this.managers.clear();
        return this;
    }

    public ManagerFactory startAllManagers() {
        loadManager(ManagersEnum.ACCOUNT, new AccountManager(this))
                .loadManager(ManagersEnum.APOSTLE, new ApostleManager(this))
                .loadManager(ManagersEnum.SESSION, new SessionManager(this))
        ;
        return this;
    }
}
