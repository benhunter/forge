package forge.ai.neural.training;

import forge.ai.neural.PolicyValueNetwork;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Coordinates dataset generation and offline training workflows.
 */
public final class NeuralTrainingPipeline {
    private final NeuralTrainingDatasetWriter datasetWriter;
    private final PolicyValueNetworkTrainer trainer;

    public NeuralTrainingPipeline(Path outputPath, PolicyValueNetworkTrainer trainer) {
        this.datasetWriter = new NeuralTrainingDatasetWriter(outputPath);
        this.trainer = trainer;
    }

    public void persistExamples(List<NeuralTrainingExample> examples) throws IOException {
        datasetWriter.appendAll(examples);
    }

    public PolicyValueNetwork train(NeuralTrainingConfig config) throws IOException {
        return trainer.train(config);
    }

    public interface PolicyValueNetworkTrainer {
        PolicyValueNetwork train(NeuralTrainingConfig config) throws IOException;
    }
}
