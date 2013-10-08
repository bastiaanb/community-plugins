package com.xebialabs.deployit.community.verifier;

public enum MustBeMapped {
    NEVER(0, 0), OPTIONALLY(0, Integer.MAX_VALUE), AT_LEAST_ONCE(1, Integer.MAX_VALUE), EXACTLY_ONCE(1, 1),
	MORE_THAN_ONCE(2, Integer.MAX_VALUE);
	
	private final int minimum;
	private final int maximum;
	
	private MustBeMapped(int minimum, int maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
    public boolean isCompliant(int value, MustBeMappedEnforcementLevel enforcementLevel) {
        switch (enforcementLevel) {
        case NONE:
            return true;
        case AT_LEAST_ONCE:
            return value >= Math.min(minimum, 1) && value <= maximum;
        default:
            return value >= minimum && value <= maximum;
        }
	}
}
