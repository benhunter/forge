package forge.ai.neural;

import forge.game.spellability.AbilitySub;
import forge.game.spellability.SpellAbility;

import java.util.Objects;

/**
 * Action representation used by the neural policy head.
 */
public final class NeuralAction {
    private final NeuralActionType type;
    private final String sourceId;
    private final String detail;

    public NeuralAction(NeuralActionType type, String sourceId, String detail) {
        this.type = Objects.requireNonNull(type, "type");
        this.sourceId = sourceId == null ? "" : sourceId;
        this.detail = detail == null ? "" : detail;
    }

    public NeuralActionType getType() {
        return type;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getDetail() {
        return detail;
    }

    public static NeuralAction forSpellAbility(SpellAbility ability) {
        String source = ability.getHostCard() == null ? "ability" : ability.getHostCard().getName();
        String detail = ability.getDescription();
        return new NeuralAction(NeuralActionType.CAST_SPELL, source, detail);
    }

    public static NeuralAction forAbilityMode(AbilitySub mode) {
        return new NeuralAction(NeuralActionType.CHOOSE_MODE, "mode", String.valueOf(mode));
    }

    public static NeuralAction forNumber(int value) {
        return new NeuralAction(NeuralActionType.CHOOSE_NUMBER, "number", Integer.toString(value));
    }

    public static NeuralAction forBoolean(boolean value) {
        return new NeuralAction(NeuralActionType.CHOOSE_BOOLEAN, "boolean", Boolean.toString(value));
    }

    @Override
    public String toString() {
        return "NeuralAction{"
            + "type=" + type
            + ", sourceId='" + sourceId + '\''
            + ", detail='" + detail + '\''
            + '}';
    }
}
