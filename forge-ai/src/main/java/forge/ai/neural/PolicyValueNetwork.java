package forge.ai.neural;

import java.util.List;

/**
 * Abstraction for neural policy/value inference.
 */
public interface PolicyValueNetwork {
    PolicyValueOutput evaluate(NeuralState state, List<NeuralAction> actionSpace);
}
