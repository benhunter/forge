package forge.ai.neural;

import forge.game.Game;
import forge.game.card.Card;
import forge.game.card.CardCollectionView;
import forge.game.phase.PhaseHandler;
import forge.game.phase.PhaseType;
import forge.game.player.Player;
import forge.game.zone.ZoneType;

import java.util.List;
import java.util.Objects;

/**
 * Encodes the current game and player into a fixed-size feature vector.
 */
public final class NeuralStateEncoder {
    public static final int FEATURE_SELF_LIFE = 0;
    public static final int FEATURE_SELF_POISON = 1;
    public static final int FEATURE_SELF_HAND = 2;
    public static final int FEATURE_SELF_LIBRARY = 3;
    public static final int FEATURE_SELF_GRAVEYARD = 4;
    public static final int FEATURE_SELF_BATTLEFIELD = 5;
    public static final int FEATURE_SELF_CREATURES = 6;
    public static final int FEATURE_SELF_LANDS = 7;
    public static final int FEATURE_SELF_ARTIFACTS = 8;
    public static final int FEATURE_SELF_ENCHANTMENTS = 9;
    public static final int FEATURE_SELF_PLANESWALKERS = 10;
    public static final int FEATURE_SELF_MANA_POOL = 11;
    public static final int FEATURE_OPP_LIFE = 12;
    public static final int FEATURE_OPP_POISON = 13;
    public static final int FEATURE_OPP_HAND = 14;
    public static final int FEATURE_OPP_LIBRARY = 15;
    public static final int FEATURE_OPP_GRAVEYARD = 16;
    public static final int FEATURE_OPP_BATTLEFIELD = 17;
    public static final int FEATURE_OPP_CREATURES = 18;
    public static final int FEATURE_OPP_LANDS = 19;
    public static final int FEATURE_OPP_ARTIFACTS = 20;
    public static final int FEATURE_OPP_ENCHANTMENTS = 21;
    public static final int FEATURE_OPP_PLANESWALKERS = 22;
    public static final int FEATURE_PHASE = 23;
    public static final int FEATURE_TURN_NUMBER = 24;
    public static final int FEATURE_ACTIVE_PLAYER = 25;
    public static final int FEATURE_VECTOR_SIZE = 26;

    public NeuralState encode(Game game, Player player) {
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(player, "player");

        float[] features = new float[FEATURE_VECTOR_SIZE];
        CardCollectionView selfBattlefield = player.getCardsIn(ZoneType.Battlefield);
        features[FEATURE_SELF_LIFE] = player.getLife();
        features[FEATURE_SELF_POISON] = player.getPoisonCounters();
        features[FEATURE_SELF_HAND] = player.getCardsIn(ZoneType.Hand).size();
        features[FEATURE_SELF_LIBRARY] = player.getCardsIn(ZoneType.Library).size();
        features[FEATURE_SELF_GRAVEYARD] = player.getCardsIn(ZoneType.Graveyard).size();
        features[FEATURE_SELF_BATTLEFIELD] = selfBattlefield.size();
        features[FEATURE_SELF_CREATURES] = countType(selfBattlefield, CardTypePredicate.CREATURE);
        features[FEATURE_SELF_LANDS] = countType(selfBattlefield, CardTypePredicate.LAND);
        features[FEATURE_SELF_ARTIFACTS] = countType(selfBattlefield, CardTypePredicate.ARTIFACT);
        features[FEATURE_SELF_ENCHANTMENTS] = countType(selfBattlefield, CardTypePredicate.ENCHANTMENT);
        features[FEATURE_SELF_PLANESWALKERS] = countType(selfBattlefield, CardTypePredicate.PLANESWALKER);
        features[FEATURE_SELF_MANA_POOL] = player.getManaPool().size();

        Player opponent = findOpponent(game, player);
        if (opponent != null) {
            CardCollectionView oppBattlefield = opponent.getCardsIn(ZoneType.Battlefield);
            features[FEATURE_OPP_LIFE] = opponent.getLife();
            features[FEATURE_OPP_POISON] = opponent.getPoisonCounters();
            features[FEATURE_OPP_HAND] = opponent.getCardsIn(ZoneType.Hand).size();
            features[FEATURE_OPP_LIBRARY] = opponent.getCardsIn(ZoneType.Library).size();
            features[FEATURE_OPP_GRAVEYARD] = opponent.getCardsIn(ZoneType.Graveyard).size();
            features[FEATURE_OPP_BATTLEFIELD] = oppBattlefield.size();
            features[FEATURE_OPP_CREATURES] = countType(oppBattlefield, CardTypePredicate.CREATURE);
            features[FEATURE_OPP_LANDS] = countType(oppBattlefield, CardTypePredicate.LAND);
            features[FEATURE_OPP_ARTIFACTS] = countType(oppBattlefield, CardTypePredicate.ARTIFACT);
            features[FEATURE_OPP_ENCHANTMENTS] = countType(oppBattlefield, CardTypePredicate.ENCHANTMENT);
            features[FEATURE_OPP_PLANESWALKERS] = countType(oppBattlefield, CardTypePredicate.PLANESWALKER);
        }

        PhaseHandler phaseHandler = game.getPhaseHandler();
        PhaseType phase = phaseHandler == null ? PhaseType.UPKEEP : phaseHandler.getPhase();
        features[FEATURE_PHASE] = phase.ordinal() / (float) PhaseType.values().length;
        if (phaseHandler != null) {
            features[FEATURE_TURN_NUMBER] = phaseHandler.getTurn() / 100f;
            features[FEATURE_ACTIVE_PLAYER] = phaseHandler.getPlayerTurn() == player ? 1f : 0f;
        }

        return new NeuralState(features);
    }

    private Player findOpponent(Game game, Player player) {
        List<Player> players = game.getPlayers();
        for (Player candidate : players) {
            if (!candidate.equals(player)) {
                return candidate;
            }
        }
        return null;
    }

    private int countType(CardCollectionView cards, CardTypePredicate predicate) {
        int count = 0;
        for (Card card : cards) {
            if (predicate.matches(card)) {
                count++;
            }
        }
        return count;
    }

    private enum CardTypePredicate {
        CREATURE {
            @Override
            boolean matches(Card card) {
                return card.getType().isCreature();
            }
        },
        LAND {
            @Override
            boolean matches(Card card) {
                return card.getType().isLand();
            }
        },
        ARTIFACT {
            @Override
            boolean matches(Card card) {
                return card.getType().isArtifact();
            }
        },
        ENCHANTMENT {
            @Override
            boolean matches(Card card) {
                return card.getType().isEnchantment();
            }
        },
        PLANESWALKER {
            @Override
            boolean matches(Card card) {
                return card.getType().isPlaneswalker();
            }
        };

        abstract boolean matches(Card card);
    }
}
