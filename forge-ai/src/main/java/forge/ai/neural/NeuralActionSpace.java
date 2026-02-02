package forge.ai.neural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class NeuralActionSpace {
    private final List<NeuralAction> actions;

    public NeuralActionSpace(List<NeuralAction> actions) {
        Objects.requireNonNull(actions, "actions");
        if (actions.isEmpty()) {
            this.actions = List.of(NeuralAction.noOp());
        } else {
            this.actions = Collections.unmodifiableList(new ArrayList<>(actions));
        }
    }

    public List<NeuralAction> getActions() {
        return actions;
    }

    public int size() {
        return actions.size();
    }

    public NeuralAction get(int index) {
        return actions.get(index);
    }
}
