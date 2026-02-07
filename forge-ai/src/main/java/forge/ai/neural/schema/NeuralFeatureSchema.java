package forge.ai.neural.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Defines the full feature schema for encoding all game state signals.
 */
public final class NeuralFeatureSchema {
    public static final int BASE_SCALAR_SIZE = 53;
    public static final int CARD_FEATURE_WIDTH = 32;
    public static final int STACK_ITEM_FEATURE_WIDTH = 24;

    public static final int MAX_BATTLEFIELD = 60;
    public static final int MAX_HAND = 15;
    public static final int MAX_GRAVEYARD = 60;
    public static final int MAX_EXILE = 60;
    public static final int MAX_STACK = 15;

    private final List<NeuralFeatureGroup> groups;
    private final int totalSize;

    private NeuralFeatureSchema(List<NeuralFeatureGroup> groups, int totalSize) {
        this.groups = groups;
        this.totalSize = totalSize;
    }

    public static NeuralFeatureSchema defaultSchema() {
        List<NeuralFeatureGroup> groups = new ArrayList<>();
        int offset = 0;

        groups.add(new NeuralFeatureGroup(
            "core_scalars",
            "Core scalar features: life, counts, phase, turn, mana pool, combat summary.",
            offset,
            BASE_SCALAR_SIZE
        ));
        offset += BASE_SCALAR_SIZE;

        int perPlayerBattlefield = MAX_BATTLEFIELD * CARD_FEATURE_WIDTH;
        groups.add(new NeuralFeatureGroup(
            "self_battlefield_cards",
            "Per-card battlefield embeddings (self).",
            offset,
            perPlayerBattlefield
        ));
        offset += perPlayerBattlefield;
        groups.add(new NeuralFeatureGroup(
            "opp_battlefield_cards",
            "Per-card battlefield embeddings (opponent).",
            offset,
            perPlayerBattlefield
        ));
        offset += perPlayerBattlefield;

        int perHand = MAX_HAND * CARD_FEATURE_WIDTH;
        groups.add(new NeuralFeatureGroup(
            "self_hand_cards",
            "Per-card hand embeddings (self).",
            offset,
            perHand
        ));
        offset += perHand;
        groups.add(new NeuralFeatureGroup(
            "opp_hand_known_cards",
            "Known opponent hand cards (unknown cards are zero-padded).",
            offset,
            perHand
        ));
        offset += perHand;

        int perGraveyard = MAX_GRAVEYARD * CARD_FEATURE_WIDTH;
        groups.add(new NeuralFeatureGroup(
            "self_graveyard_cards",
            "Per-card graveyard embeddings (self).",
            offset,
            perGraveyard
        ));
        offset += perGraveyard;
        groups.add(new NeuralFeatureGroup(
            "opp_graveyard_cards",
            "Per-card graveyard embeddings (opponent).",
            offset,
            perGraveyard
        ));
        offset += perGraveyard;

        int perExile = MAX_EXILE * CARD_FEATURE_WIDTH;
        groups.add(new NeuralFeatureGroup(
            "self_exile_cards",
            "Per-card exile embeddings (self).",
            offset,
            perExile
        ));
        offset += perExile;
        groups.add(new NeuralFeatureGroup(
            "opp_exile_cards",
            "Per-card exile embeddings (opponent).",
            offset,
            perExile
        ));
        offset += perExile;

        int stackSize = MAX_STACK * STACK_ITEM_FEATURE_WIDTH;
        groups.add(new NeuralFeatureGroup(
            "stack_items",
            "Per-stack-item embeddings (spell/ability, targets, controller).",
            offset,
            stackSize
        ));
        offset += stackSize;

        groups.add(new NeuralFeatureGroup(
            "combat_assignments",
            "Combat mapping embeddings (attackers, blockers, defenders).",
            offset,
            MAX_BATTLEFIELD * 4
        ));
        offset += MAX_BATTLEFIELD * 4;

        return new NeuralFeatureSchema(Collections.unmodifiableList(groups), offset);
    }

    public List<NeuralFeatureGroup> getGroups() {
        return groups;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public NeuralFeatureGroup getGroup(String name) {
        Objects.requireNonNull(name, "name");
        for (NeuralFeatureGroup group : groups) {
            if (group.getName().equals(name)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Unknown feature group: " + name);
    }
}
