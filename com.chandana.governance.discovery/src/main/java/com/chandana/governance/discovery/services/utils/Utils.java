package com.chandana.governance.discovery.services.utils;

import com.chandana.governance.discovery.client.ServiceAdminClient;
import com.chandana.governance.discovery.internal.DataHolder;
import com.chandana.governance.discovery.services.ServiceDiscoveryTask;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.common.AuthenticationException;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaData;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaDataWrapper;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.xml.namespace.QName;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    private static final Log log = LogFactory.getLog(ServiceDiscoveryTask.class);


    private static String userName = "admin";
    private static String password = "admin";
    private static String serverEpr = "https://localhost:9444/services/";
    private static String defaultVersion = "1.0.0";

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        Utils.userName = userName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Utils.password = password;
    }

    public static String getServerEpr() {
        return serverEpr;
    }

    public static void setServerEpr(String serverEpr) {
        Utils.serverEpr = serverEpr;
    }

    public static String getDefaultVersion() {
        return defaultVersion;
    }

    public static void setDefaultVersion(String defaultVersion) {
        Utils.defaultVersion = defaultVersion;
    }

    public static void populateServiceArtifacts() throws RegistryException {
        PrivilegedCarbonContext.startTenantFlow();
        try {
            String username = getUserName();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME, true);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(username);
            UserRegistry governance = DataHolder.getInstance().getGovernanceRegistry(username);
            GovernanceUtils.loadGovernanceArtifacts(governance, GovernanceUtils.findGovernanceArtifactConfigurations(governance));
            GenericArtifactManager artifactManager = new GenericArtifactManager(governance, "soapservice");
            ServiceMetaData[] serviceData = getServiceMetaData();
            int count = 0;
            for (final ServiceMetaData service : serviceData) {
                if (service.getActive()) {
                    if (createServcieArtifacts(artifactManager, service)) continue;

                } else {
                    createServcieArtifacts(artifactManager, service);
                }
                count++;
            }
            log.info("# of service created :" + count);
        } catch (AuthenticationException | RemoteException e) {
            throw new RegistryException("Failed to obtain proxy services from ESB node  " + getServerEpr() + e.getMessage());
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

    }

    private static boolean createServcieArtifacts(GenericArtifactManager artifactManager, ServiceMetaData service) throws GovernanceException {
        String wsdlURL = service.getWsdlURLs()[0];
        final String name = service.getName();
        final String version = getDefaultVersion();
        System.out.println(wsdlURL);
        Map<String, List<String>> listMap = new HashMap<String, List<String>>();
        //Create the search attribute map
        listMap.put("overview_name", new ArrayList<String>() {{
            add(name);
        }});
        listMap.put("overview_version", new ArrayList<String>() {{
            add(version);
        }});
        //Find the results.
        GenericArtifact[] genericArtifacts = artifactManager.findGenericArtifacts(listMap);
        if (genericArtifacts.length == 1) {
            return true;
        } else {
            GenericArtifact artifact = artifactManager.newGovernanceArtifact(new QName(service.getServiceGroupName(), service.getName()));

            artifact.setAttribute("overview_name", service.getName());
            artifact.setAttribute("overview_version", getDefaultVersion());
            artifact.setAttribute("overview_description", "This service is created with ESB G-Reg discovery task");
            artifact.setAttribute("interface_wsdlURL", service.getWsdlURLs()[0]);

            artifactManager.addGenericArtifact(artifact);
        }
        return false;
    }

    private static ServiceMetaData[] getServiceMetaData() throws AuthenticationException, RemoteException {
        String cookie = authenticate(DataHolder.getInstance().getConfigurationContext(), getServerEpr(), getUserName(), getPassword());
        ServiceAdminClient client = new ServiceAdminClient(cookie, getServerEpr(), DataHolder.getInstance().getConfigurationContext());
        ServiceMetaDataWrapper servicesInfo = client.getAllServices("ALL", "", 0);
        ServiceMetaData[] serviceData = servicesInfo.getServices();
        return serviceData;
    }

    private static String authenticate(ConfigurationContext ctx, String serviceURL, String username, String password)
            throws AxisFault, AuthenticationException {
        String cookie = null;
        String serviceEndpoint = serviceURL + "AuthenticationAdmin";

        AuthenticationAdminStub stub = new AuthenticationAdminStub(ctx, serviceEndpoint);
        ServiceClient client = stub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        try {
            boolean result = stub.login(username, password, new URL(serviceEndpoint).getHost());
            if (result) {
                cookie = (String) stub._getServiceClient().getServiceContext().getProperty(HTTPConstants.COOKIE_STRING);
            }
            return cookie;
        } catch (Exception e) {
            String msg = "Error occurred while logging in";
            throw new AuthenticationException(msg, e);
        }
    }
}
