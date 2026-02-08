package forge.ai.neural;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable feature vector for neural inference and training.
 */
public final class NeuralState {
    private final float[] features;

    public NeuralState(float[] features) {
        this.features = Objects.requireNonNull(features, "features");
    }

    public float[] getFeatures() {
        return features;
    }

    @Override
    public String toString() {
        return "NeuralState{features=" + Arrays.toString(features) + '}';
    }
}
