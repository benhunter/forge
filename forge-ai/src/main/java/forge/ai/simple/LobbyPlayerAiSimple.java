package forge.ai.simple;

import forge.LobbyPlayer;
import forge.ai.AIOption;
import forge.game.Game;
import forge.game.player.IGameEntitiesFactory;
import forge.game.player.Player;
import forge.game.player.PlayerController;

import java.util.Set;

/**
 * Simple AI as a Proof of Concept for multiple AI's in game. Reference LobbyPlayerAI for comparison.
 */
public class LobbyPlayerAiSimple extends LobbyPlayer implements IGameEntitiesFactory {

//    public LobbyPlayerAiSimple(String name) {
//        super(name);
//    }

    public LobbyPlayerAiSimple(String name, Set<AIOption> options) {
        super(name);
    }

    public void setRotateProfileEachGame(boolean equals) {
    }

    @Override
    public void hear(LobbyPlayer player, String message) {

    }

    @Override
    public PlayerController createMindSlaveController(Player master, Player slave) {
        return null;
    }

    @Override
    public Player createIngamePlayer(Game game, int id) {
        return null;
    }

    public void setAiProfile(String profile) {

    }
}
