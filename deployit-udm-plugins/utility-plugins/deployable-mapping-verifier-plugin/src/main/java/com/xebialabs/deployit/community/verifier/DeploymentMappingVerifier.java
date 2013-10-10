package com.xebialabs.deployit.community.verifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;

public class DeploymentMappingVerifier {
    public static final String REQUIRED_INSTANCES_PER_ENVIRONMENT_PROPERTY = "requiredInstancesPerEnvironment";
    public static final String REQUIRED_INSTANCES_ENFORCEMENT_PROPERTY = "requiredInstancesEnforcement";

    public List<String> validate(DeployedApplication deployedApplication) {
        final RequiredInstancesEnforcement mustBeMappedEnforcementLevel =
            deployedApplication.getEnvironment().getProperty(REQUIRED_INSTANCES_ENFORCEMENT_PROPERTY);
        final Set<Deployable> allDeployables = deployedApplication.getVersion().getDeployables();
        final Map<Deployable, Integer> deployedCountsPerDeployable = new HashMap<Deployable, Integer>(allDeployables.size());
        for (final Deployable deployabe : allDeployables) {
            deployedCountsPerDeployable.put(deployabe, 0);
        }

        for (final Deployed<?, ?> deployed : deployedApplication.getDeployeds()) {
            final Deployable deployable = deployed.getDeployable();
            deployedCountsPerDeployable.put(deployable, deployedCountsPerDeployable.get(deployable) + 1);
        }

        final List<String> errorMessages = new ArrayList<String>();

        for (Map.Entry<Deployable, Integer> entry : deployedCountsPerDeployable.entrySet()) {
            final Deployable deployable = entry.getKey();
            RequiredInstancesPerEnvironment mustBeMapped = deployable.getProperty(REQUIRED_INSTANCES_PER_ENVIRONMENT_PROPERTY);

            if (!mustBeMapped.isCompliant(entry.getValue(), mustBeMappedEnforcementLevel)) {
                errorMessages.add(String.format("deployable %s would have %d instances, but required is %s",
                    deployable.getId(), entry.getValue(), mustBeMapped));
            }
        }

        return errorMessages;
    }
}