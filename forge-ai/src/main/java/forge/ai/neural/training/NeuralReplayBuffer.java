package forge.ai.neural.training;

import forge.ai.neural.NeuralTrainingBatch;
import forge.ai.neural.NeuralTrainingSample;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NeuralReplayBuffer {
    private final Deque<NeuralTrainingSample> buffer;
    private final int capacity;
    private final Random random;

    public NeuralReplayBuffer(int capacity, Random random) {
        this.capacity = capacity;
        this.buffer = new ArrayDeque<>(capacity);
        this.random = Objects.requireNonNull(random, "random");
    }

    public void addSamples(List<NeuralTrainingSample> samples) {
        for (NeuralTrainingSample sample : samples) {
            if (buffer.size() >= capacity) {
                buffer.removeFirst();
            }
            buffer.addLast(sample);
        }
    }

    public NeuralTrainingBatch sampleBatch(int batchSize) {
        int size = Math.min(batchSize, buffer.size());
        List<NeuralTrainingSample> samples = new ArrayList<>(size);
        List<NeuralTrainingSample> snapshot = new ArrayList<>(buffer);
        for (int i = 0; i < size; i++) {
            samples.add(snapshot.get(random.nextInt(snapshot.size())));
        }
        return new NeuralTrainingBatch(samples);
    }

    public int size() {
        return buffer.size();
    }
}
