# Deployable Mapping Verifier plugin #

# Overview #

The Deployable Mapping Verifier plugin is a Deployit plugin that enables automatic verification that a deployment includes all required deployeds. Deployment will not proceed if the requirements are not met.

# Requirements #

* **Deployit**: tested with Deployit 3.9.3, your mileage may vary with older releases.

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory. 

# Usage

This plugin adds the optional property `mustBeMapped` to `udm.BaseDeployable`. This is an enum that specifies how many Deployeds a valid deployment should map from this Deployable. It can have the following values:

* NEVER. #deployeds == 0, i.e. this Deployable should not be deployed (in this Enviroment). Mostly useful when preventing deployment to a particular Environment.
* OPTIONALLY. #deployeds >= 0, this Deployable may be deployed but does not have to be.
* AT_LEAST_ONCE. #deployeds >= 1, this Deployable must be deployed.
* EXACTLY_ONCE. #deployeds == 1, this Deployable must de deployed to one Container.
* MORE_THAN_ONCE. #deployeds > 1, this Deployable must be deployed to more than one Container.

The default value for this property is `AT_LEAST_ONCE`, indicating that typically Deployables must be part of the deployment. This can be overridden in `deployit-defaults.properties` of course.

The plugin also adds an optional property `mustBeMappedEnforcementLevel` to `udm.Environment`. This specifies how strict the enforcement of the `mustBeMapped` checking should be. It can have the following values:

* NONE. Skip `mustBeMapped` checking completely.
* AT_LEAST_ONCE. Enforce `mustBeMapped` checking, but treat MORE_THAN_ONCE as AT_LEAST_ONCE. This enables scenarios where redundant deployment is mandatory for the production environment but deployemnt to a single server is sufficient for test environments. 
* MORE_THAN_ONCE. Enforce full `mustBeMapped` checking.

The default value for this property is `NONE`. This enables a gradual transition to enforcement once all applications for an environment have correct `mustBeMapped` configurations. 
