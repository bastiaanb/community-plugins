package com.xebialabs.deployit.community.validator;

public enum DeploymentCardinality {
	NONE(0, 0),
	OPTIONAL(0, Integer.MAX_VALUE),
	MANDATORY(1, Integer.MAX_VALUE),
	EXACTLY_ONE(1, 1),
	REDUNDANT(2, Integer.MAX_VALUE);
	
	private final int minimum;
	private final int maximum;
	
	private DeploymentCardinality(int minimum, int maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	public boolean isInRange(int value) {
		return value >= minimum && value <= maximum;
	}
}
