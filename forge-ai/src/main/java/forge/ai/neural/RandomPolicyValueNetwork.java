package forge.ai.neural;

import java.util.List;
import java.util.Random;

/**
 * Placeholder policy/value network using random policy and heuristic value.
 */
public final class RandomPolicyValueNetwork implements PolicyValueNetwork {
    private final Random random;

    public RandomPolicyValueNetwork(Random random) {
        this.random = random;
    }

    public RandomPolicyValueNetwork() {
        this(new Random());
    }

    @Override
    public PolicyValueOutput evaluate(NeuralState state, List<NeuralAction> actionSpace) {
        int size = Math.max(1, actionSpace.size());
        float[] policy = new float[size];
        float sum = 0f;
        for (int i = 0; i < size; i++) {
            policy[i] = 0.1f + random.nextFloat();
            sum += policy[i];
        }
        for (int i = 0; i < size; i++) {
            policy[i] /= sum;
        }
        float value = heuristicValue(state);
        return new PolicyValueOutput(policy, value);
    }

    private float heuristicValue(NeuralState state) {
        float[] features = state.getFeatures();
        if (features.length <= NeuralStateEncoder.FEATURE_OPP_LIFE) {
            return 0f;
        }
        float lifeDelta = features[NeuralStateEncoder.FEATURE_SELF_LIFE]
            - features[NeuralStateEncoder.FEATURE_OPP_LIFE];
        return Math.max(-1f, Math.min(1f, lifeDelta / 20f));
    }
}
