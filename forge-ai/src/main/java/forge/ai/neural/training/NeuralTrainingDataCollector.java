package forge.ai.neural.training;

import forge.ai.neural.NeuralDecisionEngine;
import forge.ai.neural.PolicyValueOutput;
import forge.game.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Collects decisions during play and finalizes them into training examples.
 */
public final class NeuralTrainingDataCollector {
    private final List<PendingDecision> pendingDecisions = new ArrayList<>();

    public void recordDecision(NeuralDecisionEngine.Decision decision, String context) {
        Objects.requireNonNull(decision, "decision");
        PolicyValueOutput output = decision.getOutput();
        pendingDecisions.add(new PendingDecision(
            decision.getState().getFeatures(),
            output.getPolicy(),
            decision.getSelectedIndex(),
            context
        ));
    }

    public List<NeuralTrainingExample> finalizeGame(Player winner, Player perspective) {
        float value = computeOutcomeValue(winner, perspective);
        List<NeuralTrainingExample> examples = new ArrayList<>();
        for (PendingDecision decision : pendingDecisions) {
            float[] policyTarget = new float[decision.policy.length];
            System.arraycopy(decision.policy, 0, policyTarget, 0, decision.policy.length);
            if (decision.actionIndex >= 0 && decision.actionIndex < policyTarget.length) {
                policyTarget[decision.actionIndex] = 1f;
            }
            examples.add(new NeuralTrainingExample(decision.state, policyTarget, value, decision.context));
        }
        pendingDecisions.clear();
        return examples;
    }

    private float computeOutcomeValue(Player winner, Player perspective) {
        if (winner == null || perspective == null) {
            return 0f;
        }
        return winner.equals(perspective) ? 1f : -1f;
    }

    private static final class PendingDecision {
        private final float[] state;
        private final float[] policy;
        private final int actionIndex;
        private final String context;

        private PendingDecision(float[] state, float[] policy, int actionIndex, String context) {
            this.state = state;
            this.policy = policy;
            this.actionIndex = actionIndex;
            this.context = context == null ? "" : context;
        }
    }
}
