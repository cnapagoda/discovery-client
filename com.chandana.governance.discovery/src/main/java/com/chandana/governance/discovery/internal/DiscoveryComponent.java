package com.chandana.governance.discovery.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.ntask.core.service.TaskService;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @scr.component name="org.wso2.carbon.governance.mediation"
 * immediate="true"
 * @scr.reference name="configuration.context.service"
 * interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContextService" unbind="unsetConfigurationContextService"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService" cardinality="1..1"
 * policy="dynamic" bind="setRegistryService" unbind="unsetRegistryService"
 * @scr.reference name="ntask.component" interface="org.wso2.carbon.ntask.core.service.TaskService"
 * cardinality="1..1" policy="dynamic" bind="setTaskService" unbind="unsetTaskService"
 */
public class DiscoveryComponent {
    private static final Log log = LogFactory.getLog(DiscoveryComponent.class);

    protected void activate(ComponentContext context) {
    }

    protected void setRegistryService(RegistryService registryService) {
        DataHolder.getInstance().setRegistryService(registryService);
    }

    protected void unsetRegistryService(RegistryService registryService) {
        DataHolder.getInstance().setRegistryService(null);
    }

    protected void setConfigurationContextService(
            ConfigurationContextService configurationContextService) {
        if (log.isDebugEnabled()) {
            log.debug("The Configuration Context Service was set");
        }
        if (configurationContextService != null) {
            DataHolder.getInstance().setConfigurationContext(configurationContextService.getServerConfigContext());
        }
    }

    protected void unsetConfigurationContextService(
            ConfigurationContextService configurationContextService) {
        DataHolder.getInstance().setConfigurationContext(null);
    }

    protected void setTaskService(TaskService taskService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting the Task Service");
        }
        DataHolder.getInstance().setTaskService(taskService);
    }

    protected void unsetTaskService(TaskService taskService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting the Task Service");
        }
        DataHolder.getInstance().setTaskService(null);
    }
}
