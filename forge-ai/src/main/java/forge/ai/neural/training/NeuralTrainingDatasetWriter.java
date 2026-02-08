package forge.ai.neural.training;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Writes training examples to a line-oriented dataset file.
 */
public final class NeuralTrainingDatasetWriter {
    private final Path outputPath;

    public NeuralTrainingDatasetWriter(Path outputPath) {
        this.outputPath = outputPath;
    }

    public void writeAll(List<NeuralTrainingExample> examples) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (NeuralTrainingExample example : examples) {
            builder.append(example.toRecord()).append(System.lineSeparator());
        }
        Files.writeString(outputPath, builder.toString(), StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void appendAll(List<NeuralTrainingExample> examples) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (NeuralTrainingExample example : examples) {
            builder.append(example.toRecord()).append(System.lineSeparator());
        }
        Files.writeString(outputPath, builder.toString(), StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
