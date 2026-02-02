package forge.ai.neural;

import forge.game.GameObject;
import forge.game.spellability.SpellAbility;

import java.util.Objects;
import java.util.Optional;

public final class NeuralAction {
    private final NeuralActionType type;
    private final Integer primaryId;
    private final Integer secondaryId;
    private final Integer choiceValue;
    private final String label;

    private NeuralAction(NeuralActionType type, Integer primaryId, Integer secondaryId, Integer choiceValue, String label) {
        this.type = Objects.requireNonNull(type, "type");
        this.primaryId = primaryId;
        this.secondaryId = secondaryId;
        this.choiceValue = choiceValue;
        this.label = label;
    }

    public static NeuralAction forSpellAbility(SpellAbility ability) {
        Objects.requireNonNull(ability, "ability");
        return new NeuralAction(
            NeuralActionType.PLAY_SPELL_ABILITY,
            ability.getId(),
            ability.getHostCard() != null ? ability.getHostCard().getId() : null,
            null,
            ability.toString()
        );
    }

    public static NeuralAction forTarget(GameObject target) {
        Objects.requireNonNull(target, "target");
        return new NeuralAction(
            NeuralActionType.CHOOSE_TARGET,
            target.getId(),
            null,
            null,
            target.toString()
        );
    }

    public static NeuralAction forChoice(NeuralActionType type, int choiceValue, String label) {
        return new NeuralAction(type, null, null, choiceValue, label);
    }

    public static NeuralAction noOp() {
        return new NeuralAction(NeuralActionType.NO_OP, null, null, null, "NO_OP");
    }

    public NeuralActionType getType() {
        return type;
    }

    public Optional<Integer> getPrimaryId() {
        return Optional.ofNullable(primaryId);
    }

    public Optional<Integer> getSecondaryId() {
        return Optional.ofNullable(secondaryId);
    }

    public Optional<Integer> getChoiceValue() {
        return Optional.ofNullable(choiceValue);
    }

    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }
}
