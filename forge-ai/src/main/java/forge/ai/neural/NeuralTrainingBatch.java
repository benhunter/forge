package forge.ai.neural;

import java.util.List;
import java.util.Objects;

public final class NeuralTrainingBatch {
    private final List<NeuralTrainingSample> samples;

    public NeuralTrainingBatch(List<NeuralTrainingSample> samples) {
        this.samples = List.copyOf(Objects.requireNonNull(samples, "samples"));
    }

    public List<NeuralTrainingSample> getSamples() {
        return samples;
    }
}
