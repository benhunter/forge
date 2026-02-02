package forge.ai.mcts;

import forge.ai.ComputerUtilAbility;
import forge.ai.simulation.GameSimulator;
import forge.ai.simulation.GameStateEvaluator;
import forge.ai.simulation.SimulationController;
import forge.game.Game;
import forge.game.card.CardCollection;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MctsSearch {
    private static final int DEFAULT_MAX_ROLLOUT_DEPTH = 3;

    private final Random random = new Random();
    private int maxIterations = 200;
    private long timeLimitMs = 200;
    private double explorationConstant = Math.sqrt(2.0);
    private int maxRolloutDepth = DEFAULT_MAX_ROLLOUT_DEPTH;
    private MctsRolloutPolicy rolloutPolicy = (game, aiPlayer, actions, rng) -> actions.get(rng.nextInt(actions.size()));

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public void setTimeLimitMs(long timeLimitMs) {
        this.timeLimitMs = timeLimitMs;
    }

    public void setExplorationConstant(double explorationConstant) {
        this.explorationConstant = explorationConstant;
    }

    public void setMaxRolloutDepth(int maxRolloutDepth) {
        this.maxRolloutDepth = maxRolloutDepth;
    }

    public void setRolloutPolicy(MctsRolloutPolicy rolloutPolicy) {
        this.rolloutPolicy = rolloutPolicy;
    }

    public SpellAbility search(Game game, Player aiPlayer) {
        List<SpellAbility> rootActions = getCandidateActions(game, aiPlayer);
        if (rootActions.isEmpty()) {
            return null;
        }
        MctsNode root = new MctsNode(null, null, game, aiPlayer, rootActions);
        long startTime = System.currentTimeMillis();

        int iterations = 0;
        while (iterations < maxIterations && (System.currentTimeMillis() - startTime) < timeLimitMs) {
            MctsNode selected = select(root);
            MctsNode expanded = expand(selected);
            double score = simulate(expanded);
            backpropagate(expanded, score);
            iterations++;
        }

        return root.getChildren().stream()
            .max(Comparator.comparingDouble(MctsNode::getMeanScore))
            .map(MctsNode::getActionFromParent)
            .orElse(rootActions.get(0));
    }

    private MctsNode select(MctsNode node) {
        MctsNode current = node;
        while (!current.isTerminal() && current.isFullyExpanded()) {
            current = selectChildWithUct(current);
        }
        return current;
    }

    private MctsNode selectChildWithUct(MctsNode node) {
        double logParentVisits = Math.log(Math.max(1, node.getVisitCount()));
        return node.getChildren().stream()
            .max(Comparator.comparingDouble(child -> uctScore(child, logParentVisits)))
            .orElseThrow(() -> new IllegalStateException("No children to select."));
    }

    private double uctScore(MctsNode node, double logParentVisits) {
        if (node.getVisitCount() == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return node.getMeanScore() + explorationConstant * Math.sqrt(logParentVisits / node.getVisitCount());
    }

    private MctsNode expand(MctsNode node) {
        SpellAbility action = node.pollUntriedAction();
        if (action == null) {
            return node;
        }
        GameSimulator simulator = createSimulator(node.getGame(), node.getAiPlayer());
        GameStateEvaluator.Score score = simulator.simulateSpellAbility(action);
        Game simGame = simulator.getSimGame();
        Player simAiPlayer = simulator.getSimAiPlayer();
        List<SpellAbility> actions = getCandidateActions(simGame, simAiPlayer);
        MctsNode child = new MctsNode(node, action, simGame, simAiPlayer, actions);
        child.updateStats(score.value);
        node.addChild(child);
        return child;
    }

    private double simulate(MctsNode node) {
        if (node.isTerminal()) {
            return scoreGame(node.getGame(), node.getAiPlayer());
        }
        Game currentGame = node.getGame();
        Player currentAi = node.getAiPlayer();
        double score = scoreGame(currentGame, currentAi);
        int depth = 0;
        while (depth < maxRolloutDepth && !currentGame.isGameOver()) {
            List<SpellAbility> actions = getCandidateActions(currentGame, currentAi);
            if (actions.isEmpty()) {
                break;
            }
            SpellAbility action = rolloutPolicy.chooseAction(currentGame, currentAi, actions, random);
            GameSimulator simulator = createSimulator(currentGame, currentAi);
            GameStateEvaluator.Score simScore = simulator.simulateSpellAbility(action);
            currentGame = simulator.getSimGame();
            currentAi = simulator.getSimAiPlayer();
            score = simScore.value;
            depth++;
        }
        return score;
    }

    private void backpropagate(MctsNode node, double score) {
        MctsNode current = node;
        while (current != null) {
            current.updateStats(score);
            current = current.getParent();
        }
    }

    private GameSimulator createSimulator(Game game, Player aiPlayer) {
        GameStateEvaluator evaluator = new GameStateEvaluator();
        GameStateEvaluator.Score score = evaluator.getScoreForGameState(game, aiPlayer);
        SimulationController controller = new SimulationController(score);
        return new GameSimulator(controller, game, aiPlayer, null);
    }

    private double scoreGame(Game game, Player aiPlayer) {
        GameStateEvaluator evaluator = new GameStateEvaluator();
        return evaluator.getScoreForGameState(game, aiPlayer).value;
    }

    private List<SpellAbility> getCandidateActions(Game game, Player aiPlayer) {
        List<SpellAbility> actions = new ArrayList<>();
        CardCollection lands = ComputerUtilAbility.getAvailableLandsToPlay(game, aiPlayer);
        if (lands != null && !lands.isEmpty()) {
            actions.addAll(ComputerUtilAbility.getSpellAbilities(lands, aiPlayer));
        }
        CardCollection otherCards = new CardCollection(aiPlayer.getCardsIn(ZoneType.Hand));
        actions.addAll(ComputerUtilAbility.getSpellAbilities(otherCards, aiPlayer));
        actions.removeIf(sa -> sa == null);
        return actions;
    }
}
