package forge.ai.mcts;

import forge.ai.simulation.GameSimulator;
import forge.ai.simulation.GameStateEvaluator;
import forge.ai.simulation.SimulationController;
import forge.ai.simulation.SpellAbilityPicker;
import forge.game.Game;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;

import java.util.List;
import java.util.Random;

public class MctsRolloutPolicy {
    private final Random random;
    private final GameStateEvaluator evaluator;

    public MctsRolloutPolicy() {
        this.random = new Random();
        this.evaluator = new GameStateEvaluator();
    }

    public double rollout(Game game, Player player, int maxDepth) {
        if (game == null || player == null) {
            return 0.0;
        }
        Game currentGame = game;
        Player currentPlayer = player;
        double score = evaluator.getScoreForGameState(currentGame, currentPlayer).value;
        for (int depth = 0; depth < maxDepth; depth++) {
            if (currentGame.isGameOver()) {
                break;
            }
            SpellAbilityPicker picker = new SpellAbilityPicker(currentGame, currentPlayer);
            List<SpellAbility> actions = picker.getCandidateSpellsAndAbilities();
            if (actions.isEmpty()) {
                break;
            }
            SpellAbility action = actions.get(random.nextInt(actions.size()));
            SimulationController controller = new SimulationController(evaluator.getScoreForGameState(currentGame, currentPlayer));
            GameSimulator simulator = new GameSimulator(controller, currentGame, currentPlayer, null);
            GameStateEvaluator.Score simScore = simulator.simulateSpellAbility(action);
            if (simScore == null) {
                break;
            }
            Game nextGame = simulator.getSimulatedGameState();
            Player nextPlayer = (Player) simulator.getGameCopier().find(currentPlayer);
            if (nextPlayer == null) {
                break;
            }
            currentGame = nextGame;
            currentPlayer = nextPlayer;
            score = simScore.value;
        }
        return score;
    }
}
