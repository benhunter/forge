package forge.ai.neural;

import java.util.Arrays;
import java.util.Objects;

/**
 * Output of policy/value inference.
 */
public final class PolicyValueOutput {
    private final float[] policy;
    private final float value;

    public PolicyValueOutput(float[] policy, float value) {
        this.policy = Objects.requireNonNull(policy, "policy");
        this.value = value;
    }

    public float[] getPolicy() {
        return policy;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PolicyValueOutput{policy=" + Arrays.toString(policy) + ", value=" + value + '}';
    }
}
