package forge.ai.neural;

import forge.ai.neural.schema.NeuralFeatureSchema;
import forge.card.MagicColor;
import forge.card.mana.ManaAtom;
import forge.game.Game;
import forge.game.card.Card;
import forge.game.card.CardCollectionView;
import forge.game.card.CounterEnumType;
import forge.game.combat.Combat;
import forge.game.mana.ManaPool;
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
    public static final int FEATURE_SELF_ENERGY = 26;
    public static final int FEATURE_SELF_EXPERIENCE = 27;
    public static final int FEATURE_SELF_EXILE = 28;
    public static final int FEATURE_SELF_TAPPED_PERMANENTS = 29;
    public static final int FEATURE_SELF_UNTAPPED_LANDS = 30;
    public static final int FEATURE_SELF_SICK_CREATURES = 31;
    public static final int FEATURE_SELF_MANA_WHITE = 32;
    public static final int FEATURE_SELF_MANA_BLUE = 33;
    public static final int FEATURE_SELF_MANA_BLACK = 34;
    public static final int FEATURE_SELF_MANA_RED = 35;
    public static final int FEATURE_SELF_MANA_GREEN = 36;
    public static final int FEATURE_SELF_MANA_COLORLESS = 37;
    public static final int FEATURE_OPP_ENERGY = 38;
    public static final int FEATURE_OPP_EXPERIENCE = 39;
    public static final int FEATURE_OPP_EXILE = 40;
    public static final int FEATURE_OPP_TAPPED_PERMANENTS = 41;
    public static final int FEATURE_OPP_UNTAPPED_LANDS = 42;
    public static final int FEATURE_OPP_SICK_CREATURES = 43;
    public static final int FEATURE_OPP_MANA_WHITE = 44;
    public static final int FEATURE_OPP_MANA_BLUE = 45;
    public static final int FEATURE_OPP_MANA_BLACK = 46;
    public static final int FEATURE_OPP_MANA_RED = 47;
    public static final int FEATURE_OPP_MANA_GREEN = 48;
    public static final int FEATURE_OPP_MANA_COLORLESS = 49;
    public static final int FEATURE_STACK_SIZE = 50;
    public static final int FEATURE_COMBAT_ATTACKERS = 51;
    public static final int FEATURE_COMBAT_BLOCKERS = 52;
    public static final int FEATURE_VECTOR_SIZE = 53;

    public NeuralState encode(Game game, Player player) {
        return encode(game, player, NeuralFeatureSchema.defaultSchema());
    }

    public NeuralState encode(Game game, Player player, NeuralFeatureSchema schema) {
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(schema, "schema");

        float[] features = new float[schema.getTotalSize()];
        features(features, player, FEATURE_SELF_LIFE, FEATURE_SELF_POISON, FEATURE_SELF_HAND, FEATURE_SELF_LIBRARY, FEATURE_SELF_GRAVEYARD, FEATURE_SELF_BATTLEFIELD, FEATURE_SELF_CREATURES, FEATURE_SELF_LANDS, FEATURE_SELF_ARTIFACTS, FEATURE_SELF_ENCHANTMENTS, FEATURE_SELF_PLANESWALKERS);
        CardCollectionView selfBattlefield = player.getCardsIn(ZoneType.Battlefield);
        features[FEATURE_SELF_MANA_POOL] = player.getManaPool().totalMana();
        features[FEATURE_SELF_ENERGY] = player.getCounters(CounterEnumType.ENERGY);
        features[FEATURE_SELF_EXPERIENCE] = player.getCounters(CounterEnumType.EXPERIENCE);
        features[FEATURE_SELF_EXILE] = player.getCardsIn(ZoneType.Exile).size();
        features[FEATURE_SELF_TAPPED_PERMANENTS] = countTapped(selfBattlefield);
        features[FEATURE_SELF_UNTAPPED_LANDS] = countUntappedLands(selfBattlefield);
        features[FEATURE_SELF_SICK_CREATURES] = countSickCreatures(selfBattlefield);
        fillManaPool(features, FEATURE_SELF_MANA_WHITE, player.getManaPool());

        Player opponent = findOpponent(game, player);
        if (opponent != null) {
            features(features, opponent, FEATURE_OPP_LIFE, FEATURE_OPP_POISON, FEATURE_OPP_HAND, FEATURE_OPP_LIBRARY, FEATURE_OPP_GRAVEYARD, FEATURE_OPP_BATTLEFIELD, FEATURE_OPP_CREATURES, FEATURE_OPP_LANDS, FEATURE_OPP_ARTIFACTS, FEATURE_OPP_ENCHANTMENTS, FEATURE_OPP_PLANESWALKERS);
            CardCollectionView oppBattlefield = opponent.getCardsIn(ZoneType.Battlefield);
            features[FEATURE_OPP_ENERGY] = opponent.getCounters(CounterEnumType.ENERGY);
            features[FEATURE_OPP_EXPERIENCE] = opponent.getCounters(CounterEnumType.EXPERIENCE);
            features[FEATURE_OPP_EXILE] = opponent.getCardsIn(ZoneType.Exile).size();
            features[FEATURE_OPP_TAPPED_PERMANENTS] = countTapped(oppBattlefield);
            features[FEATURE_OPP_UNTAPPED_LANDS] = countUntappedLands(oppBattlefield);
            features[FEATURE_OPP_SICK_CREATURES] = countSickCreatures(oppBattlefield);
            fillManaPool(features, FEATURE_OPP_MANA_WHITE, opponent.getManaPool());
        }

        PhaseHandler phaseHandler = game.getPhaseHandler();
        PhaseType phase = phaseHandler == null ? PhaseType.UPKEEP : phaseHandler.getPhase();
        features[FEATURE_PHASE] = phase.ordinal() / (float) PhaseType.values().length;
        if (phaseHandler != null) {
            features[FEATURE_TURN_NUMBER] = phaseHandler.getTurn() / 100f;
            features[FEATURE_ACTIVE_PLAYER] = phaseHandler.getPlayerTurn() == player ? 1f : 0f;
        }
        features[FEATURE_STACK_SIZE] = game.getStack().size();
        Combat combat = game.getCombat();
        if (combat != null) {
            features[FEATURE_COMBAT_ATTACKERS] = combat.getAttackers().size();
            features[FEATURE_COMBAT_BLOCKERS] = countCombatBlockers(combat);
        }

        return new NeuralState(features);
    }

    private void features(float[] features, Player player, int featurePlayerLife, int featurePlayerPoisonCounters, int featureCardsInHandSize, int featureCardsInLibrarySize, int featureCardsInGraveyardSize, int featureCardsInBattlefieldSize, int featurePlayerCreatures, int featurePlayerLands, int featurePlayerArtifacts, int featurePlayerEnchantments, int featurePlayerPlaneswalkers) {
        CardCollectionView playerBattlefield = player.getCardsIn(ZoneType.Battlefield);
        features[featurePlayerLife] = player.getLife();
        features[featurePlayerPoisonCounters] = player.getPoisonCounters();
        features[featureCardsInHandSize] = player.getCardsIn(ZoneType.Hand).size();
        features[featureCardsInLibrarySize] = player.getCardsIn(ZoneType.Library).size();
        features[featureCardsInGraveyardSize] = player.getCardsIn(ZoneType.Graveyard).size();
        features[featureCardsInBattlefieldSize] = playerBattlefield.size();
        features[featurePlayerCreatures] = countType(playerBattlefield, CardTypePredicate.CREATURE);
        features[featurePlayerLands] = countType(playerBattlefield, CardTypePredicate.LAND);
        features[featurePlayerArtifacts] = countType(playerBattlefield, CardTypePredicate.ARTIFACT);
        features[featurePlayerEnchantments] = countType(playerBattlefield, CardTypePredicate.ENCHANTMENT);
        features[featurePlayerPlaneswalkers] = countType(playerBattlefield, CardTypePredicate.PLANESWALKER);
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

    private int countTapped(CardCollectionView cards) {
        int count = 0;
        for (Card card : cards) {
            if (card.isTapped()) {
                count++;
            }
        }
        return count;
    }

    private int countUntappedLands(CardCollectionView cards) {
        int count = 0;
        for (Card card : cards) {
            if (card.getType().isLand() && !card.isTapped()) {
                count++;
            }
        }
        return count;
    }

    private int countSickCreatures(CardCollectionView cards) {
        int count = 0;
        for (Card card : cards) {
            if (card.isSick()) {
                count++;
            }
        }
        return count;
    }

    private void fillManaPool(float[] features, int offset, ManaPool manaPool) {
        if (manaPool == null) {
            return;
        }
        features[offset] = manaPool.getAmountOfColor(MagicColor.WHITE);
        features[offset + 1] = manaPool.getAmountOfColor(MagicColor.BLUE);
        features[offset + 2] = manaPool.getAmountOfColor(MagicColor.BLACK);
        features[offset + 3] = manaPool.getAmountOfColor(MagicColor.RED);
        features[offset + 4] = manaPool.getAmountOfColor(MagicColor.GREEN);
        features[offset + 5] = manaPool.getAmountOfColor(MagicColor.COLORLESS);
    }

    private int countCombatBlockers(Combat combat) {
        int count = 0;
        for (Card attacker : combat.getAttackers()) {
            CardCollectionView blockers = combat.getBlockers(attacker);
            count += blockers.size();
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
