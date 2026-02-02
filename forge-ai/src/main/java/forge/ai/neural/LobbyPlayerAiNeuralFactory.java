package forge.ai.neural;

import forge.ai.AIOption;
import forge.ai.common.LobbyPlayerAi;
import forge.game.Game;
import forge.game.player.IGameEntitiesFactory;
import forge.game.player.Player;
import forge.game.player.PlayerController;

import java.util.Set;

/**
 * Factory for creating neural-network AI players.
 */
public class LobbyPlayerAiNeuralFactory extends LobbyPlayerAi implements IGameEntitiesFactory {

    public LobbyPlayerAiNeuralFactory(String name, Set<AIOption> options) {
        super(name);
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
        PlayerController controller = new PlayerControllerAiNeural(game, player, this);
        player.setFirstController(controller);

        return player;
    }

    public void setAiProfile(String profile) {

    }
}
