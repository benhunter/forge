package forge.ai.neural.training;

import forge.ai.neural.NeuralTrainingSample;

import java.util.List;

public interface NeuralSelfPlayGenerator {
    List<NeuralTrainingSample> generateEpisodes(int episodeCount);
}
