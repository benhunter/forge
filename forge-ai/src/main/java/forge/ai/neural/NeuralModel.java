package forge.ai.neural;

public interface NeuralModel {
    NeuralPolicyOutput predict(NeuralState state, NeuralActionSpace actionSpace);

    default void update(NeuralTrainingBatch batch) {
        // default no-op for inference-only implementations
    }
}
