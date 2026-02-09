package forge.ai.mcts;

import forge.game.Game;
import forge.game.player.Player;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.expectThrows;

public class LobbyPlayerAiMctsFactoryTest {

    @Test
    public void createIngamePlayer_shouldApplyConfig() {
        Helpers.initializeLocalizer();
        LobbyPlayerAiMctsFactory factory = new LobbyPlayerAiMctsFactory("TestAI", null, 123, 456, 7, 0.9);
        Game game = Helpers.createGame();

        Player player = factory.createIngamePlayer(game, 0);
        assertNotNull(player);

        PlayerControllerAiMcts controller = (PlayerControllerAiMcts) player.getController();
        assertEquals(controller.getIterationBudget(), 123);
        assertEquals(controller.getTimeLimitMs(), 456);
        assertEquals(controller.getRolloutDepth(), 7);
        assertEquals(controller.getExplorationConstant(), 0.9);
    }

    @Test
    public void createIngamePlayer_shouldThrowException_WhenNameIsNull() {
        LobbyPlayerAiMctsFactory factory = new LobbyPlayerAiMctsFactory(null, null);
        IllegalStateException exception = expectThrows(IllegalStateException.class, () -> factory.createIngamePlayer(null, 0));
        assertEquals(exception.getMessage(), "Player name is null");
    }

    @Test
    public void createIngamePlayer_shouldThrowException_WhenGameIsNull() {
        LobbyPlayerAiMctsFactory factory = new LobbyPlayerAiMctsFactory("TestAI", null);
        IllegalStateException exception = expectThrows(IllegalStateException.class, () -> factory.createIngamePlayer(null, 0));
        assertEquals(exception.getMessage(), "Game is null");
    }

    @Test
    public void createIngamePlayer_shouldThrowException_WhenIdIsNegative() {
        LobbyPlayerAiMctsFactory factory = new LobbyPlayerAiMctsFactory("TestAI", null);
        Game game = Helpers.createGame();
        IllegalStateException exception = expectThrows(IllegalStateException.class, () -> factory.createIngamePlayer(game, -1));
        assertEquals(exception.getMessage(), "Player id is negative");
    }
}
