package com.chandana.governance.discovery.internal;

import org.apache.axis2.context.ConfigurationContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.ntask.core.TaskManager;
import org.wso2.carbon.ntask.core.service.TaskService;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;


public class DataHolder {

    private TaskManager taskManager;

    private RegistryService registryService;

    private ConfigurationContext configurationContext;

    private TaskService taskService;

    private static final DataHolder INSTANCE = new DataHolder();

    private DataHolder() {
    }

    public static DataHolder getInstance() {
        return INSTANCE;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public ConfigurationContext getConfigurationContext() {
        return configurationContext;
    }

    public void setConfigurationContext(ConfigurationContext configurationContext) {
        this.configurationContext = configurationContext;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public UserRegistry getGovernanceRegistry(String user) throws RegistryException {
        if (user != null) {
            return this.registryService.getGovernanceUserRegistry(user, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId());
        } else {
            return this.registryService.getGovernanceSystemRegistry(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId());
        }
    }
}
