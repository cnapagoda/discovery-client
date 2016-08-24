package com.chandana.governance.discovery.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.chandana.governance.discovery.services.utils.Utils;
import org.wso2.carbon.ntask.core.Task;

import java.util.Map;

public class ServiceDiscoveryTask  implements Task {

    private static final Log log = LogFactory.getLog(ServiceDiscoveryTask.class);

    @Override
    public void setProperties(Map<String, String> properties) {
        Utils.setUserName(properties.get("userName"));
        Utils.setPassword(properties.get("password"));
        Utils.setServerEpr(properties.get("serverUrl"));
        Utils.setDefaultVersion(properties.get("version"));
    }

    @Override
    public void init() {
        log.info("ServiceDiscoveryTask initialized..");
    }

    @Override
    public void execute() {
        try {
            Utils.populateServiceArtifacts();
        } catch (Exception e) {
            log.error("Error while performing ServiceDiscoveryTask " + e.getMessage());
        }
    }
}
