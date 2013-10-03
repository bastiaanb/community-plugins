package com.xebialabs.deployit.community.validator;

import java.util.HashSet;
import java.util.Set;

import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.validation.ValidationContext;

public class CompleteDeploymentValidator implements com.xebialabs.deployit.plugin.api.validation.Validator<DeployedApplication> {
    public void validate(DeployedApplication deployedApplication, ValidationContext context) {
    	Set<Deployable> deployedDeployables = new HashSet<Deployable>();
    	for (Deployed<?, ?> deployed : deployedApplication.getDeployeds()) {
    		deployedDeployables.add(deployed.getDeployable());
    	}
    	Set<Deployable> unmappedDeployables = (new HashSet<Deployable>(deployedApplication.getVersion().getDeployables()));
    	unmappedDeployables.removeAll(deployedDeployables);
    	
    	for(Deployable unmappedDeployable : unmappedDeployables) {
    		context.error("deployable %s is not mapped to a deployed", unmappedDeployable.getId());
    	}
    }
}