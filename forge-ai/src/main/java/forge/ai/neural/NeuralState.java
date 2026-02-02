package forge.ai.neural;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class NeuralState {
    private final float[] features;
    private final List<String> featureNames;

    public NeuralState(float[] features, List<String> featureNames) {
        this.features = Arrays.copyOf(Objects.requireNonNull(features, "features"), features.length);
        this.featureNames = Collections.unmodifiableList(List.copyOf(Objects.requireNonNull(featureNames, "featureNames")));
    }

    public float[] getFeatures() {
        return Arrays.copyOf(features, features.length);
    }

    public List<String> getFeatureNames() {
        return featureNames;
    }

    public int size() {
        return features.length;
    }
}
