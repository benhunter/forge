package forge.ai.neural.training;

import java.nio.file.Path;

/**
 * Configuration for offline neural training jobs.
 */
public final class NeuralTrainingConfig {
    private final Path datasetPath;
    private final int epochs;
    private final int batchSize;

    public NeuralTrainingConfig(Path datasetPath, int epochs, int batchSize) {
        this.datasetPath = datasetPath;
        this.epochs = epochs;
        this.batchSize = batchSize;
    }

    public Path getDatasetPath() {
        return datasetPath;
    }

    public int getEpochs() {
        return epochs;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
