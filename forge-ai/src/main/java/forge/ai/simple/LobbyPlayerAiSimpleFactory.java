package forge.ai.simple;

import forge.ai.AIOption;
import forge.ai.common.LobbyPlayerAi;
import forge.game.Game;
import forge.game.player.IGameEntitiesFactory;
import forge.game.player.Player;
import forge.game.player.PlayerController;

import java.util.Set;

/**
 * A factory to create simple AI players.
 *
 * Simple AI as a Proof of Concept for multiple AI's in game. Reference LobbyPlayerAI for comparison.
 */
// TODO: why is this a factory and a LobbyPlayer? can it just be the AI player?
public class LobbyPlayerAiSimpleFactory extends LobbyPlayerAi implements IGameEntitiesFactory {

    public LobbyPlayerAiSimpleFactory(String name, Set<AIOption> options) {
        super(name);
    }

    // TODO: does this belong in LobbyPlayerAi an abstract method?
    public void setRotateProfileEachGame(boolean equals) {
        // Ignore
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
        PlayerController controller = new PlayerControllerAiSimple(game, player, this);
        player.setFirstController(controller);

        return player;
    }

    // TODO: refactor to use this method which takes name
//    public Player createIngamePlayer(String name, Game game, int id) {
//        return new Player(name, game, id);
//    }

    public void setAiProfile(String profile) {

    }
}
