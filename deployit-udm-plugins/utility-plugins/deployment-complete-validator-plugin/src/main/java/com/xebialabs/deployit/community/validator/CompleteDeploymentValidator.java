package com.xebialabs.deployit.community.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.validation.ValidationContext;

public class CompleteDeploymentValidator implements com.xebialabs.deployit.plugin.api.validation.Validator<DeployedApplication> {
	public static final DeploymentCardinality DEFAULT_DEPLOYMENT_CARDINALITY = DeploymentCardinality.MANDATORY;
	public static final String DEPLOYMENT_CARDINALITY = "deploymentCardinality";
	public static final String IGNORE_CARDINALITY_REQUIREMENTS = "ignoreCardinalityRequirements";
	public static final String IGNORE_REDUNDANCY_REQUIREMENTS = "ignoreRedundancyRequirements";
	
    public void validate(DeployedApplication deployedApplication, ValidationContext context) {
    	if (Boolean.TRUE.equals(deployedApplication.getEnvironment().getProperty(IGNORE_CARDINALITY_REQUIREMENTS))) {
    		return;
    	}
    	
    	final boolean ignoreRedundancyRequirements = Boolean.TRUE.equals(deployedApplication.getEnvironment().getProperty(IGNORE_REDUNDANCY_REQUIREMENTS));
    	
    	final Set<Deployable> allDeployables = deployedApplication.getVersion().getDeployables();
    	
    	final Map<Deployable, Integer> deployedCounts = new HashMap<Deployable, Integer>(allDeployables.size());    	
    	for (final Deployable deployabe : allDeployables) {
    		deployedCounts.put(deployabe, 0);
    	}

    	for (final Deployed<?, ?> deployed : deployedApplication.getDeployeds()) {
    		final Deployable deployable = deployed.getDeployable();
    		deployedCounts.put(deployable, deployedCounts.get(deployable) + 1);
    	}

    	for (Map.Entry<Deployable, Integer> entry : deployedCounts.entrySet()) {
    		final Deployable deployable = entry.getKey();
    		
    		DeploymentCardinality cardinality = DEFAULT_DEPLOYMENT_CARDINALITY;
    		if (deployable.hasProperty(DEPLOYMENT_CARDINALITY)) {
    			cardinality = deployable.getProperty(DEPLOYMENT_CARDINALITY);
    		}

    		if (ignoreRedundancyRequirements && cardinality.equals(DeploymentCardinality.REDUNDANT)) {
    			cardinality = DeploymentCardinality.MANDATORY;
    		}
    		
    		if (!cardinality.isInRange(entry.getValue())) {
    			context.error("deployable %s would be deployed %d times, but required cardinality is %s", deployable.getId(), entry.getValue(), cardinality);
    		}
    	}
    }
}