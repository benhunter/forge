package forge.ai.neural;

import java.util.Arrays;

public class RandomPolicyModel implements NeuralModel {
    @Override
    public NeuralPolicyOutput predict(NeuralState state, NeuralActionSpace actionSpace) {
        float[] logits = new float[actionSpace.size()];
        Arrays.fill(logits, 0f);
        return new NeuralPolicyOutput(logits, 0f);
    }
}
