package com.xebialabs.deployit.community.validator;

import java.util.ArrayList;
import java.util.List;

import com.xebialabs.deployit.plugin.api.deployment.planning.PostPlanProcessor;
import com.xebialabs.deployit.plugin.api.deployment.specification.DeltaSpecification;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.validation.ValidationContext;

public class EnsureCompleteDeployment {
	private static final CompleteDeploymentValidator validator = new CompleteDeploymentValidator();

	@PostPlanProcessor
	public static List<Step> validate(DeltaSpecification spec) {
		DeployedApplication deployedApplication = spec.getDeployedApplication();
		SimpleValidationContext context = new SimpleValidationContext();

		validator.validate(deployedApplication, context);

		if (!context.getMessages().isEmpty()) {
            throw new IllegalArgumentException(buildErrorMessage(deployedApplication, context.getMessages()));
		}

		return null;
	}

	private static String buildErrorMessage(DeployedApplication deployedApplication, List<String> messages) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Cannot deploy '").append(deployedApplication.getName())
        .append("' (version ").append(deployedApplication.getVersion().getVersion())
        .append(") to '").append(deployedApplication.getEnvironment().getName())
        .append("' as the following cardinality requirements are not met:");
        for (String message : messages) {
            errorMessage.append("\n- '").append(message).append("'");
        }
        return errorMessage.toString();
	}

	private static class SimpleValidationContext implements ValidationContext {
		private final List<String> messages = new ArrayList<String>();
		
		@Override
		public void error(String message, Object... parameters) {
			messages.add(String.format(message, parameters));				
		}
		
		public List<String> getMessages() {
			return messages;
		}
	}
}
