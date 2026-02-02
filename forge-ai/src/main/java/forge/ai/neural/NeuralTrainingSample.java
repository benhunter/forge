package forge.ai.neural;

import java.util.Arrays;
import java.util.Objects;

public final class NeuralTrainingSample {
    private final NeuralState state;
    private final float[] policyTarget;
    private final float valueTarget;

    public NeuralTrainingSample(NeuralState state, float[] policyTarget, float valueTarget) {
        this.state = Objects.requireNonNull(state, "state");
        this.policyTarget = Arrays.copyOf(Objects.requireNonNull(policyTarget, "policyTarget"), policyTarget.length);
        this.valueTarget = valueTarget;
    }

    public NeuralState getState() {
        return state;
    }

    public float[] getPolicyTarget() {
        return Arrays.copyOf(policyTarget, policyTarget.length);
    }

    public float getValueTarget() {
        return valueTarget;
    }
}
