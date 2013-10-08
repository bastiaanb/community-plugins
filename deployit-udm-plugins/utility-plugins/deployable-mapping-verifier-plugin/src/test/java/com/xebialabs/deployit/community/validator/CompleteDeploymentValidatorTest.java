package com.xebialabs.deployit.community.validator;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.Environment;
import com.xebialabs.deployit.plugin.api.udm.Version;
import com.xebialabs.deployit.plugin.api.validation.ValidationContext;

public class CompleteDeploymentValidatorTest {

	@Mock
    private DeployedApplication deployedApplication;
	
	@Mock
	private Environment environment;

	@Mock
	private ValidationContext context;

	@SuppressWarnings("rawtypes")
	private Deployed deployed1;

	@SuppressWarnings("rawtypes")
	private Deployed deployed2;

	@SuppressWarnings("rawtypes")
	private Deployed deployed2b;

	private Deployable deployable1;
	
	private Deployable deployable2;
	
	private CompleteDeploymentValidator validator;
	
	@Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);

        deployable1 = mockDeployable("Applications/completeDeploymentTest/1.0/test-command-1");
        deployable2 = mockDeployable("Applications/completeDeploymentTest/1.0/test-command-2");
        
        final Version version = mock(Version.class);
        when(version.getId()).thenReturn("Applications/completeDeploymentTest/1.0");        
        when(version.getDeployables()).thenReturn(new HashSet<Deployable>(Arrays.asList(deployable1, deployable2)));

        when(deployedApplication.getVersion()).thenReturn(version);
        when(deployedApplication.getEnvironment()).thenReturn(environment);
        
        deployed1 = mockDeployed("Infrastructure/test-server-1/test-command-1", deployable1);
        deployed2 = mockDeployed("Infrastructure/test-server-1/test-command-2", deployable2);
        deployed2b = mockDeployed("Infrastructure/test-server-2/test-command-2", deployable2);
        
		validator = new CompleteDeploymentValidator();		
    }
    
	@SuppressWarnings("rawtypes")
	@Test
	public void completeDeploymentShouldPassValidation() {
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));

		validator.validate(deployedApplication, context);

		verifyValidatePasses();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void incompleteDeploymentShouldFailValidation() {
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));

		validator.validate(deployedApplication, context);

		verifyValidateFails();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void incompleteDeploymentShouldPassValidationIfDeployableCardinalityIsOptional() {
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));
		mockCiProperty(deployable2, "requiredDeploymentCardinality", DeploymentCardinality.OPTIONAL);

		validator.validate(deployedApplication, context);

		verifyValidatePasses();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void redundantDeploymentShouldPassValidationIfDeployableCardinalityIsRedudant() {
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2, deployed2b)));
		mockCiProperty(deployable2, "requiredDeploymentCardinality", DeploymentCardinality.REDUNDANT);

		validator.validate(deployedApplication, context);

		verifyValidatePasses();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void nonRedundantDeploymentShouldFailValidationIfDeployableCardinalityIsRedudant() {
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));
		mockCiProperty(deployable2, "requiredDeploymentCardinality", DeploymentCardinality.REDUNDANT);

		validator.validate(deployedApplication, context);
		
		verifyValidateFails();

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void nonRedundantDeploymentShouldPassValidationIfDeployableCardinalityIsRedudantAndEnvironmentIgnoresRedundancyRequirements() {
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));
		mockCiProperty(deployable2, "requiredDeploymentCardinality", DeploymentCardinality.REDUNDANT);
		mockCiProperty(environment, "ignoreRedundancyRequirements", true);

		validator.validate(deployedApplication, context);
		
		verifyValidatePasses();

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void incompleteDeploymentShouldPassValidationIfEnvironmentIgnoresCardinalityRequirements() {
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));
		mockCiProperty(environment, "ignoreCardinalityRequirements", true);

		validator.validate(deployedApplication, context);
		
		verifyValidatePasses();
	}

	private void verifyValidatePasses() {
		verify(context, never()).error(anyString(), anyVararg());
		
	}

	private void verifyValidateFails() {
		verify(context, atLeastOnce()).error(anyString(), anyVararg());		
	}
	
	private void mockCiProperty(ConfigurationItem ci, String name, Object value) {
		when(ci.hasProperty(name)).thenReturn(true);
		when(ci.getProperty(name)).thenReturn(value);
	}
	
	private Deployable mockDeployable(final String id) {
        Deployable deployable = mock(Deployable.class);
        when(deployable.getId()).thenReturn(id);
        return deployable;
	}

	@SuppressWarnings("rawtypes")
	private Deployed mockDeployed(final String id, final Deployable deployable) {
		Deployed deployed = mock(Deployed.class);
		when(deployed.getId()).thenReturn(id);
		when(deployed.getDeployable()).thenReturn(deployable);
		return deployed;
	}
}
