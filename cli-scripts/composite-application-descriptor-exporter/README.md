# Composite Application Descriptor Exporter #

This document describes the functionality provided by the Composite Application Descriptor Exporter.

See the **Deployit Reference Manual** for background information on Deployit and deployment concepts.

# Overview #

The Composite Application Descriptor Exporter is a Deployit CLI extension that can export a Composite Application Descriptor ('CAD') for a CompositePackage in Deployit's repository. 

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 3.7

# Installation #

Place the export-composite-application-descriptor.py script in the `CLI_HOME/ext` directory.

# Usage

Start the CLI and invoke the `exportCompositePackage` function. 

# Examples

`manifestexporter.export('Applications/ComposedApplication/1.0', '/home/me/ComposedApplication-1.0.cad')`

will export the CAD for ComposedApplcation/1.0 to `/home/me/ComposedApplication-1.0.cad'`.
