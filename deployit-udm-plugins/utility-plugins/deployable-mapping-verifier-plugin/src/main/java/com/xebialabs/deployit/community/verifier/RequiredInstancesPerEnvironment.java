package com.xebialabs.deployit.community.verifier;

public enum RequiredInstancesPerEnvironment {
    EXACTLY_ZERO(0, 0),
    ANY(0, Integer.MAX_VALUE),
    AT_LEAST_ONE(1, Integer.MAX_VALUE),
    EXACTLY_ONE(1, 1),
    MORE_THAN_ONE(2, Integer.MAX_VALUE);

    private final int minimum;
    private final int maximum;

    private RequiredInstancesPerEnvironment(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public boolean isCompliant(int value, RequiredInstancesEnforcement enforcementLevel) {
        switch (enforcementLevel) {
        case NONE:
            return true;
        case AT_LEAST_ONE:
            return value >= Math.min(minimum, 1) && value <= maximum;
        default:
            return value >= minimum && value <= maximum;
        }
    }
}
