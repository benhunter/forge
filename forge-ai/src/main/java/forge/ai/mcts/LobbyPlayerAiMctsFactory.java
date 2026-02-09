package forge.ai.mcts;

import forge.ai.AIOption;
import forge.ai.common.LobbyPlayerAi;
import forge.game.Game;
import forge.game.player.IGameEntitiesFactory;
import forge.game.player.Player;
import forge.game.player.PlayerController;

import java.util.Set;

public class LobbyPlayerAiMctsFactory extends LobbyPlayerAi implements IGameEntitiesFactory {
    private final int iterationBudget;
    private final long timeLimitMs;
    private final int rolloutDepth;
    private final double explorationConstant;

    public LobbyPlayerAiMctsFactory(String name, Set<AIOption> options) {
        this(name, options, PlayerControllerAiMcts.DEFAULT_ITERATION_BUDGET,
                PlayerControllerAiMcts.DEFAULT_TIME_LIMIT_MS,
                PlayerControllerAiMcts.DEFAULT_ROLLOUT_DEPTH,
                PlayerControllerAiMcts.DEFAULT_EXPLORATION_CONSTANT);
    }

    public LobbyPlayerAiMctsFactory(String name, Set<AIOption> options, int iterationBudget, long timeLimitMs,
                                   int rolloutDepth, double explorationConstant) {
        super(name);
        this.iterationBudget = iterationBudget;
        this.timeLimitMs = timeLimitMs;
        this.rolloutDepth = rolloutDepth;
        this.explorationConstant = explorationConstant;
    }

    @Override
    public PlayerController createMindSlaveController(Player master, Player slave) {
        return null;
    }

    @Override
    public Player createIngamePlayer(Game game, int id) {
        if (this.getName() == null) {
            throw new IllegalStateException("Player name is null");
        }
        if (game == null) {
            throw new IllegalStateException("Game is null");
        }
        if (id < 0) {
            throw new IllegalStateException("Player id is negative");
        }

        Player player = new Player(name, game, id);
        PlayerControllerAiMcts controller = new PlayerControllerAiMcts(game, player, this);
        controller.setIterationBudget(iterationBudget);
        controller.setTimeLimitMs(timeLimitMs);
        controller.setRolloutDepth(rolloutDepth);
        controller.setExplorationConstant(explorationConstant);
        player.setFirstController(controller);
        return player;
    }
}
