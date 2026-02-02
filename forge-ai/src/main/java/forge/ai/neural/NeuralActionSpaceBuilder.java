package forge.ai.neural;

import forge.game.GameObject;
import forge.game.spellability.SpellAbility;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class NeuralActionSpaceBuilder {
    public NeuralActionSpace fromSpellAbilities(List<SpellAbility> abilities) {
        List<NeuralAction> actions = new ArrayList<>();
        for (SpellAbility ability : abilities) {
            actions.add(NeuralAction.forSpellAbility(ability));
        }
        return new NeuralActionSpace(actions);
    }

    public NeuralActionSpace fromTargets(List<Pair<?, GameObject>> targets) {
        List<NeuralAction> actions = new ArrayList<>();
        for (Pair<?, GameObject> target : targets) {
            actions.add(NeuralAction.forTarget(target.getRight()));
        }
        return new NeuralActionSpace(actions);
    }

    public NeuralActionSpace fromChoiceValues(NeuralActionType type, List<Integer> values, String labelPrefix) {
        List<NeuralAction> actions = new ArrayList<>();
        for (Integer value : values) {
            actions.add(NeuralAction.forChoice(type, value, labelPrefix + value));
        }
        return new NeuralActionSpace(actions);
    }
}
