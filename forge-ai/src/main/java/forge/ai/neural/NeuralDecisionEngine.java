package forge.ai.neural;

import forge.game.Game;
import forge.game.player.Player;

import java.util.List;
import java.util.Objects;

/**
 * Orchestrates state encoding and policy/value inference for action selection.
 */
public final class NeuralDecisionEngine {
    private final NeuralStateEncoder encoder;
    private final PolicyValueNetwork network;

    public NeuralDecisionEngine(NeuralStateEncoder encoder, PolicyValueNetwork network) {
        this.encoder = Objects.requireNonNull(encoder, "encoder");
        this.network = Objects.requireNonNull(network, "network");
    }

    public Decision selectAction(Game game, Player player, List<NeuralAction> actions) {
        NeuralState state = encoder.encode(game, player);
        PolicyValueOutput output = network.evaluate(state, actions);
        int selectedIndex = selectIndex(output.getPolicy());
        return new Decision(state, actions, output, selectedIndex);
    }

    private int selectIndex(float[] policy) {
        int best = 0;
        float bestValue = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < policy.length; i++) {
            if (policy[i] > bestValue) {
                bestValue = policy[i];
                best = i;
            }
        }
        return best;
    }

    public static final class Decision {
        private final NeuralState state;
        private final List<NeuralAction> actions;
        private final PolicyValueOutput output;
        private final int selectedIndex;

        public Decision(NeuralState state, List<NeuralAction> actions, PolicyValueOutput output, int selectedIndex) {
            this.state = state;
            this.actions = actions;
            this.output = output;
            this.selectedIndex = selectedIndex;
        }

        public NeuralState getState() {
            return state;
        }

        public List<NeuralAction> getActions() {
            return actions;
        }

        public PolicyValueOutput getOutput() {
            return output;
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }
    }
}
