# Deployment complete validator plugin #

# Overview #

The Deployment Complete Validator plugin is a Deployit plugin that enables automatic enforcement of completeness of deployments. If a deployment does not include
all required deployed the deployment will not proceed.

# Requirements #

* **Deployit**: tested with Deployit 3.9.3, your mileage may vary with older releases.

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

# Usage

This plugin adds the optional property `deploymentCardinality` to `udm.Deployable`. This is an enum that specifies how many Deployeds the deployment should generate for 
this Deployable. It can have the following values:

* NONE. cardinality == 0, i.e. this Deployable should not be deployed. Mostly useful when preventing deployment to a particular Environment.
* OPTIONAL. cardinality >= 0
* MANDATORY. cardinality >= 1
* EXACTLY_ONE. cardinality == 1
* REDUNDANT. cardinality > 1, i.e. this Deployable should be deployed to more than one Container.

The default value for this property is `MANDATORY`, but can be overridden in `deployit-defaults.properties` of course.

The plugin also adds two boolean properties to `udm.Environment' that relaxes the cardinality requirements for deployement to that particular environment:

* ignoreRedundancyRequirements. Relaxes `REDUNDANT` to `MANDATORY`.
* ignoreCardinalityRequirements. Disables the validation completely.

The default value for both properties is `false`.
