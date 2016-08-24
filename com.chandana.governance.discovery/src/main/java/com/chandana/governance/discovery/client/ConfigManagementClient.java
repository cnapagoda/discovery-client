package com.chandana.governance.discovery.client;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.mediation.configadmin.stub.ConfigServiceAdminStub;

import java.rmi.RemoteException;


public class ConfigManagementClient {

    private static final Log log = LogFactory.getLog(ConfigManagementClient.class);

    private ConfigServiceAdminStub stub;

    public ConfigManagementClient(String cookie,
                                  String backendServerURL,
                                  ConfigurationContext configCtx) throws AxisFault {

        String serviceURL = backendServerURL + "ConfigServiceAdmin";
        stub = new ConfigServiceAdminStub(configCtx, serviceURL);
        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setTimeOutInMilliSeconds(15 * 60 * 1000);
        option.setProperty(HTTPConstants.SO_TIMEOUT, 15 * 60 * 1000);
        option.setProperty(HTTPConstants.CONNECTION_TIMEOUT, 15 * 60 * 1000);
        option.setManageSession(true);
        option.setProperty(HTTPConstants.COOKIE_STRING, cookie);

    }

    public String getSynapseConfig() throws RemoteException {
        return stub.getConfiguration();

    }


}
