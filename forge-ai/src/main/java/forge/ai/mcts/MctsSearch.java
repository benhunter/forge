package forge.ai.mcts;

import forge.ai.simulation.GameCopier;
import forge.ai.simulation.GameSimulator;
import forge.ai.simulation.GameStateEvaluator;
import forge.ai.simulation.MultiTargetSelector;
import forge.ai.simulation.SimulationController;
import forge.ai.simulation.SpellAbilityPicker;
import forge.game.Game;
import forge.game.GameEntity;
import forge.game.GameObject;
import forge.game.card.Card;
import forge.game.card.CardCollection;
import forge.game.card.CardCollectionView;
import forge.game.combat.Combat;
import forge.game.combat.CombatUtil;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.spellability.SpellAbilityStackInstance;
import forge.util.ITriggerEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MctsSearch {
    private static final int MAX_TARGET_SAMPLES = 8;
    private final Game game;
    private final Player player;
    private final GameStateEvaluator evaluator;
    private final Random random;
    private final MctsRolloutPolicy rolloutPolicy;
    private int iterationBudget = 100;
    private long timeLimitMs = 50;
    private double explorationConstant = 1.4;
    private int rolloutDepth = 2;

    public MctsSearch(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.evaluator = new GameStateEvaluator();
        this.random = new Random();
        this.rolloutPolicy = new MctsRolloutPolicy();
    }

    public SpellAbility chooseAbilityToPlay(Card hostCard, List<SpellAbility> abilities, ITriggerEvent triggerEvent) {
        if (abilities == null || abilities.isEmpty()) {
            return null;
        }
        MctsNode root = new MctsNode(game, player, new ArrayList<>(abilities), null, null, null);
        long endTime = timeLimitMs > 0 ? System.currentTimeMillis() + timeLimitMs : Long.MAX_VALUE;
        int iterations = 0;
        while (iterations < iterationBudget && System.currentTimeMillis() < endTime) {
            iterations++;
            MctsNode node = root;
            while (node.isFullyExpanded() && node.hasChildren()) {
                MctsNode next = node.selectChildUct(explorationConstant);
                if (next == null) {
                    break;
                }
                node = next;
            }
            if (node.hasUntriedActions()) {
                SpellAbility action = node.popUntriedAction(random);
                if (action != null) {
                    SimulationResult result = simulateAction(node, action);
                    if (result != null) {
                        node = node.addChild(action, result.game, result.player, result.candidateActions, null);
                    }
                }
            }
            double score = rolloutPolicy.rollout(node.getGame(), node.getPlayer(), rolloutDepth);
            backpropagate(node, score);
        }
        SpellAbility bestAbility = selectBestAction(root);
        return bestAbility != null ? bestAbility : abilities.get(0);
    }

    public boolean chooseTargetsFor(SpellAbility currentAbility) {
        if (!currentAbility.usesTargeting()) {
            return true;
        }

        MultiTargetSelector selector = new MultiTargetSelector(currentAbility, null);
        if (!selector.hasPossibleTargets()) {
            return false;
        }

        selector.reset();
        GameStateEvaluator.Score bestScore = null;
        MultiTargetSelector.Targets bestTargets = null;
        int evaluated = 0;
        while (selector.selectNextTargets() && evaluated < MAX_TARGET_SAMPLES) {
            evaluated++;
            GameStateEvaluator.Score score = simulateAbility(currentAbility);
            if (score != null && (bestScore == null || score.value > bestScore.value)) {
                bestScore = score;
                bestTargets = selector.getLastSelectedTargets();
            }
        }

        if (bestTargets == null) {
            selector.reset();
            return selector.selectNextTargets();
        }

        selector.reset();
        return selector.selectTargets(bestTargets);
    }

    public Pair<SpellAbilityStackInstance, GameObject> chooseTarget(SpellAbility sa, List<Pair<SpellAbilityStackInstance, GameObject>> allTargets) {
        if (allTargets == null || allTargets.isEmpty()) {
            return null;
        }
        Pair<SpellAbilityStackInstance, GameObject> best = null;
        int bestScore = Integer.MIN_VALUE;
        for (Pair<SpellAbilityStackInstance, GameObject> option : allTargets) {
            int score = scoreTarget(option.getRight());
            if (best == null || score > bestScore) {
                best = option;
                bestScore = score;
            }
        }
        return best != null ? best : allTargets.get(0);
    }

    public void declareAttackers(Player attacker, Combat combat) {
        if (combat == null) {
            return;
        }
        combat.clearAttackers();
        List<Card> possibleAttackers = CombatUtil.getPossibleAttackers(attacker);
        if (possibleAttackers.isEmpty()) {
            return;
        }
        GameEntity defender = chooseDefender(combat, attacker);
        if (defender == null) {
            return;
        }
        List<Card> selectedAttackers = chooseBestAttackers(attacker, defender, possibleAttackers);
        for (Card attackerCard : selectedAttackers) {
            combat.addAttacker(attackerCard, defender);
        }
    }

    public void declareBlockers(Player defender, Combat combat) {
        if (combat == null) {
            return;
        }
        for (Card blocker : combat.getAllBlockers()) {
            combat.undoBlockingAssignment(blocker);
        }
        CardCollection attackers = combat.getAttackers();
        if (attackers.isEmpty()) {
            return;
        }
        List<Card> availableBlockers = new ArrayList<>(defender.getCreaturesInPlay());
        for (Card attacker : attackers) {
            Card chosen = chooseFirstBlocker(attacker, availableBlockers, combat);
            if (chosen != null) {
                combat.addBlocker(attacker, chosen);
                if (!chosen.canBlockAny()) {
                    availableBlockers.remove(chosen);
                }
            }
        }
    }

    public Map<Card, Integer> assignCombatDamage(Card attacker, CardCollectionView blockers, CardCollectionView remaining, int damageDealt, GameEntity defender, boolean overrideOrder) {
        if (blockers == null || blockers.isEmpty()) {
            return Map.of();
        }
        return Map.of(blockers.get(0), damageDealt);
    }

    public CardCollection orderBlockers(Card attacker, CardCollection blockers) {
        return blockers;
    }

    public CardCollection orderBlocker(Card attacker, Card blocker, CardCollection oldBlockers) {
        return oldBlockers;
    }

    public CardCollection orderAttackers(Card blocker, CardCollection attackers) {
        return attackers;
    }

    public void setIterationBudget(int iterationBudget) {
        this.iterationBudget = iterationBudget;
    }

    public void setTimeLimitMs(long timeLimitMs) {
        this.timeLimitMs = timeLimitMs;
    }

    public void setExplorationConstant(double explorationConstant) {
        this.explorationConstant = explorationConstant;
    }

    public void setRolloutDepth(int rolloutDepth) {
        this.rolloutDepth = rolloutDepth;
    }

    private GameStateEvaluator.Score simulateAbility(SpellAbility ability) {
        if (ability == null) {
            return null;
        }
        GameStateEvaluator.Score baseScore = evaluator.getScoreForGameState(game, player);
        SimulationController controller = new SimulationController(baseScore);
        GameSimulator simulator = new GameSimulator(controller, game, player, null);
        return simulator.simulateSpellAbility(ability);
    }

    private GameEntity chooseDefender(Combat combat, Player attacker) {
        GameEntity best = null;
        int lowestLife = Integer.MAX_VALUE;
        for (GameEntity defender : combat.getDefenders()) {
            if (defender instanceof Player targetPlayer) {
                int life = targetPlayer.getLife();
                if (life < lowestLife) {
                    best = defender;
                    lowestLife = life;
                }
            } else if (best == null) {
                best = defender;
            }
        }
        return best;
    }

    private List<Card> chooseBestAttackers(Player attacker, GameEntity defender, List<Card> candidates) {
        int noAttackScore = evaluateAttackPlan(defender, List.of());
        int allAttackScore = evaluateAttackPlan(defender, candidates);
        return allAttackScore >= noAttackScore ? candidates : List.of();
    }

    private int evaluateAttackPlan(GameEntity defender, List<Card> attackers) {
        GameCopier copier = new GameCopier(game);
        Game copyGame = copier.makeCopy(null, player);
        Player copyPlayer = (Player) copier.find(player);
        Combat copyCombat = copyGame.getPhaseHandler().getCombat();
        if (copyCombat == null) {
            return evaluator.getScoreForGameState(copyGame, copyPlayer).value;
        }
        GameEntity copyDefender = (GameEntity) copier.find((GameObject) defender);
        for (Card attacker : attackers) {
            Card copyCard = (Card) copier.find(attacker);
            if (copyDefender != null) {
                copyCombat.addAttacker(copyCard, copyDefender);
            }
        }
        return evaluator.getScoreForGameState(copyGame, copyPlayer).value;
    }

    private Card chooseFirstBlocker(Card attacker, List<Card> blockers, Combat combat) {
        for (Card blocker : blockers) {
            if (CombatUtil.canBlock(attacker, blocker, combat)) {
                return blocker;
            }
        }
        return null;
    }

    private int scoreTarget(GameObject target) {
        if (target instanceof Card card) {
            return evaluator.evalCard(game, player, card);
        }
        return 0;
    }

    private SimulationResult simulateAction(MctsNode node, SpellAbility action) {
        GameStateEvaluator.Score baseScore = evaluator.getScoreForGameState(node.getGame(), node.getPlayer());
        SimulationController controller = new SimulationController(baseScore);
        GameSimulator simulator = new GameSimulator(controller, node.getGame(), node.getPlayer(), null);
        GameStateEvaluator.Score score = simulator.simulateSpellAbility(action);
        if (score == null) {
            return null;
        }
        Game simGame = simulator.getSimulatedGameState();
        Player simPlayer = (Player) simulator.getGameCopier().find(node.getPlayer());
        if (simPlayer == null) {
            return null;
        }
        List<SpellAbility> candidateActions = new SpellAbilityPicker(simGame, simPlayer).getCandidateSpellsAndAbilities();
        return new SimulationResult(simGame, simPlayer, candidateActions, score.value);
    }

    private void backpropagate(MctsNode node, double score) {
        MctsNode current = node;
        while (current != null) {
            current.recordScore(score);
            current = current.getParent();
        }
    }

    private SpellAbility selectBestAction(MctsNode root) {
        SpellAbility bestAbility = null;
        int bestVisits = -1;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (MctsNode child : root.getChildren()) {
            if (child.getVisitCount() > bestVisits) {
                bestVisits = child.getVisitCount();
                bestScore = child.getMeanScore();
                bestAbility = child.getIncomingAction();
            } else if (child.getVisitCount() == bestVisits && child.getMeanScore() > bestScore) {
                bestScore = child.getMeanScore();
                bestAbility = child.getIncomingAction();
            }
        }
        return bestAbility;
    }

    private static class SimulationResult {
        private final Game game;
        private final Player player;
        private final List<SpellAbility> candidateActions;
        private final double score;

        private SimulationResult(Game game, Player player, List<SpellAbility> candidateActions, double score) {
            this.game = game;
            this.player = player;
            this.candidateActions = candidateActions;
            this.score = score;
        }
    }
}
