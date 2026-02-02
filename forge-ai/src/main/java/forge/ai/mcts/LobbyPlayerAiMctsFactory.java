package forge.ai.mcts;

import forge.ai.AIOption;
import forge.ai.common.LobbyPlayerAi;
import forge.game.Game;
import forge.game.player.IGameEntitiesFactory;
import forge.game.player.Player;
import forge.game.player.PlayerController;

import java.util.Set;

/**
 * Factory for MCTS-based AI players.
 */
public class LobbyPlayerAiMctsFactory extends LobbyPlayerAi implements IGameEntitiesFactory {
    private boolean useSimulation;

    public LobbyPlayerAiMctsFactory(String name, Set<AIOption> options) {
        super(name);
        if (options != null && options.contains(AIOption.USE_SIMULATION)) {
            this.useSimulation = true;
        }
    }

    private PlayerControllerAiMcts createControllerFor(Player ai) {
        PlayerControllerAiMcts controller = new PlayerControllerAiMcts(ai.getGame(), ai, this);
        controller.setUseSimulation(useSimulation);
        return controller;
    }

    @Override
    public PlayerController createMindSlaveController(Player master, Player slave) {
        return createControllerFor(slave);
    }

    @Override
    public Player createIngamePlayer(Game game, int id) {
        if (getName() == null) {
            throw new IllegalStateException("Player name is null");
        }
        if (game == null) {
            throw new IllegalStateException("Game is null");
        }
        if (id < 0) {
            throw new IllegalStateException("Player id is negative");
        }

        Player player = new Player(getName(), game, id);
        player.setFirstController(createControllerFor(player));
        return player;
    }
}
