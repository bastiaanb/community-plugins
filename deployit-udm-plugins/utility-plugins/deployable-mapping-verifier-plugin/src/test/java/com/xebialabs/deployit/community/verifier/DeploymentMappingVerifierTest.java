package com.xebialabs.deployit.community.verifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.Environment;
import com.xebialabs.deployit.plugin.api.udm.Version;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeploymentMappingVerifierTest {

	@Mock
    private DeployedApplication deployedApplication;
	
	@Mock
	private Environment environment;

	@SuppressWarnings("rawtypes")
	private Deployed deployed1;

	@SuppressWarnings("rawtypes")
	private Deployed deployed2;

	@SuppressWarnings("rawtypes")
	private Deployed deployed2b;

	private Deployable deployable1;
	
	private Deployable deployable2;
	
	private DeploymentMappingVerifier verifier;
	
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
        
		verifier = new DeploymentMappingVerifier();		
    }
    
	@SuppressWarnings("rawtypes")
	@Test
	public void completeDeploymentShouldPassValidation() {
        setMustBeMapped(deployable1, MustBeMapped.AT_LEAST_ONCE);
        setMustBeMapped(deployable2, MustBeMapped.AT_LEAST_ONCE);
        setEnforcementLevel(environment, MustBeMappedEnforcementLevel.MORE_THAN_ONCE);
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
	}

    @SuppressWarnings("rawtypes")
	@Test
	public void incompleteDeploymentShouldFailValidation() {
        setMustBeMapped(deployable1, MustBeMapped.AT_LEAST_ONCE);
        setMustBeMapped(deployable2, MustBeMapped.AT_LEAST_ONCE);
        setEnforcementLevel(environment, MustBeMappedEnforcementLevel.MORE_THAN_ONCE);
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertFalse(errorMessages.isEmpty());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void incompleteDeploymentShouldPassValidationIfDeployableCardinalityIsOptional() {
        setMustBeMapped(deployable1, MustBeMapped.AT_LEAST_ONCE);
        setMustBeMapped(deployable2, MustBeMapped.OPTIONALLY);
        setEnforcementLevel(environment, MustBeMappedEnforcementLevel.MORE_THAN_ONCE);
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void redundantDeploymentShouldPassValidationIfDeployableCardinalityIsRedudant() {
        setMustBeMapped(deployable1, MustBeMapped.AT_LEAST_ONCE);
        setMustBeMapped(deployable2, MustBeMapped.MORE_THAN_ONCE);
        setEnforcementLevel(environment, MustBeMappedEnforcementLevel.MORE_THAN_ONCE);
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2, deployed2b)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void nonRedundantDeploymentShouldFailValidationIfDeployableCardinalityIsRedudant() {
        setMustBeMapped(deployable1, MustBeMapped.AT_LEAST_ONCE);
        setMustBeMapped(deployable2, MustBeMapped.MORE_THAN_ONCE);
        setEnforcementLevel(environment, MustBeMappedEnforcementLevel.MORE_THAN_ONCE);
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertFalse(errorMessages.isEmpty());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void nonRedundantDeploymentShouldPassValidationIfDeployableCardinalityIsRedudantAndEnvironmentIgnoresRedundancyRequirements() {
        setMustBeMapped(deployable1, MustBeMapped.AT_LEAST_ONCE);
        setMustBeMapped(deployable2, MustBeMapped.MORE_THAN_ONCE);
        setEnforcementLevel(environment, MustBeMappedEnforcementLevel.AT_LEAST_ONCE);
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void incompleteDeploymentShouldPassValidationIfEnvironmentIgnoresCardinalityRequirements() {
        setMustBeMapped(deployable1, MustBeMapped.AT_LEAST_ONCE);
        setMustBeMapped(deployable2, MustBeMapped.AT_LEAST_ONCE);
        setEnforcementLevel(environment, MustBeMappedEnforcementLevel.NONE);
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));

        List<String> errorMessages = verifier.validate(deployedApplication);

        assertTrue(errorMessages.isEmpty());
	}

    private void setEnforcementLevel(Environment environment, MustBeMappedEnforcementLevel enforcementLevel) {
        when(environment.hasProperty("mustBeMappedEnforcementLevel")).thenReturn(true);
        when(environment.getProperty("mustBeMappedEnforcementLevel")).thenReturn(enforcementLevel);
    }

    private void setMustBeMapped(Deployable deployable, MustBeMapped mustBeMapped) {
        when(deployable.hasProperty("mustBeMapped")).thenReturn(true);
        when(deployable.getProperty("mustBeMapped")).thenReturn(mustBeMapped);
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
