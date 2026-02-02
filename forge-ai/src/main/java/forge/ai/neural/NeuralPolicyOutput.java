package forge.ai.neural;

import java.util.Arrays;
import java.util.Objects;

public final class NeuralPolicyOutput {
    private final float[] logits;
    private final float value;

    public NeuralPolicyOutput(float[] logits, float value) {
        this.logits = Objects.requireNonNull(logits, "logits");
        this.value = value;
    }

    public float[] getLogits() {
        return logits;
    }

    public float getValue() {
        return value;
    }

    public NeuralPolicyOutput withLogits(float[] nextLogits) {
        return new NeuralPolicyOutput(Arrays.copyOf(nextLogits, nextLogits.length), value);
    }
}
