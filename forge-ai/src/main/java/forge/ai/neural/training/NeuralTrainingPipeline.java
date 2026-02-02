package forge.ai.neural.training;

import forge.ai.neural.NeuralModel;
import forge.ai.neural.NeuralTrainingBatch;
import forge.ai.neural.NeuralTrainingSample;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NeuralTrainingPipeline {
    private final NeuralModel model;
    private final NeuralTrainingConfig config;
    private final NeuralReplayBuffer replayBuffer;

    public NeuralTrainingPipeline(NeuralModel model, NeuralTrainingConfig config, Random random) {
        this.model = Objects.requireNonNull(model, "model");
        this.config = Objects.requireNonNull(config, "config");
        this.replayBuffer = new NeuralReplayBuffer(config.getReplayBufferSize(), random);
    }

    public void runIteration(NeuralSelfPlayGenerator generator) {
        List<NeuralTrainingSample> samples = generator.generateEpisodes(config.getEpisodesPerIteration());
        replayBuffer.addSamples(samples);
        NeuralTrainingBatch batch = replayBuffer.sampleBatch(config.getBatchSize());
        model.update(batch);
    }

    public int getReplayBufferSize() {
        return replayBuffer.size();
    }
}
