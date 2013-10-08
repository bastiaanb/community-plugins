package com.xebialabs.deployit.community.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xebialabs.deployit.plugin.api.validation.Rule;


@Retention(RetentionPolicy.RUNTIME)
@Rule(clazz = CompleteDeploymentValidator.class, type = "complete-deployment")
@Target(ElementType.TYPE)
public @interface CompleteDeployment {
}