package com.xebialabs.deployit.community.validator;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
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

import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.Version;
import com.xebialabs.deployit.plugin.api.validation.ValidationContext;

public class CompleteDeploymentValidatorTest {

	@Mock
    private DeployedApplication deployedApplication;
	
	@Mock
	private ValidationContext context;

	@SuppressWarnings("rawtypes")
	private Deployed deployed1;

	@SuppressWarnings("rawtypes")
	private Deployed deployed2;

	private CompleteDeploymentValidator validator;
	
	@Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);

        final Deployable deployable1 = mockDeployable("Applications/completeDeploymentTest/1.0/test-command-1");
        final Deployable deployable2 = mockDeployable("Applications/completeDeploymentTest/1.0/test-command-2");
        
        final Version version = mock(Version.class);
        when(version.getId()).thenReturn("Applications/completeDeploymentTest/1.0");        
        when(version.getDeployables()).thenReturn(new HashSet<Deployable>(Arrays.asList(deployable1, deployable2)));

        when(deployedApplication.getVersion()).thenReturn(version);

        deployed1 = mockDeployed("Infrastructure/test-server-1/test-command-1", deployable1);
        deployed2 = mockDeployed("Infrastructure/test-server-1/test-command-2", deployable2);
        
		validator = new CompleteDeploymentValidator();		
    }
    
	@SuppressWarnings("rawtypes")
	@Test
	public void completeDeploymentShouldPassValidation() {
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1, deployed2)));

		validator.validate(deployedApplication, context);

		verify(context, never()).error(anyString(), anyObject());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void incompleteDeploymentShouldFailValidation() {
		when(deployedApplication.getDeployeds()).thenReturn(new HashSet<Deployed>(Arrays.asList(deployed1)));

		validator.validate(deployedApplication, context);

		verify(context, atLeastOnce()).error(anyString(), anyObject());
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
