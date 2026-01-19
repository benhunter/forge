package forge.ai.simple;

import forge.game.Game;
import forge.game.player.Player;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LobbyPlayerAiSimpleFactoryTest {

    /**
     * Test method for {@link forge.ai.simple.LobbyPlayerAiSimpleFactory#LobbyPlayerAiSimpleFactory(java.lang.String, java.util.Set)}.
     *
     * Test that the constructor creates an instance correctly.
     *
     * Test requires Localizer to be initialized.
     */
    @Test
    public void createIngamePlayer_ShouldSucceed_WhenProvidedGameAndId() {
        Helpers.initializeLocalizer();

        LobbyPlayerAiSimpleFactory factory = new LobbyPlayerAiSimpleFactory("TestAI", null);
        assertNotNull(factory);
        assertEquals(factory.getName(), "TestAI");

        Game game = Helpers.createGame();

        assertNotNull(factory.createIngamePlayer(game, 0));
    }

    @Test
    public void createIngamePlayer_ShouldThrowException_WhenNameIsNull() {
        LobbyPlayerAiSimpleFactory factory = new LobbyPlayerAiSimpleFactory(null, null);
        IllegalStateException exception = expectThrows(IllegalStateException.class, () -> factory.createIngamePlayer(null, 0));
        assertEquals(exception.getMessage(), "Player name is null");
    }

    @Test
    public void createIngamePlayer_ShouldThrowException_WhenGameIsNull() {
        LobbyPlayerAiSimpleFactory factory = new LobbyPlayerAiSimpleFactory("TestAI", null);
        IllegalStateException exception = expectThrows(IllegalStateException.class, () -> factory.createIngamePlayer(null, 0));
        assertEquals(exception.getMessage(), "Game is null");
    }

    @Test
    public void createIngamePlayer_ShouldThrowException_WhenIdIsNegative() {
        LobbyPlayerAiSimpleFactory factory = new LobbyPlayerAiSimpleFactory("TestAI", null);
        Game game = Helpers.createGame();

        IllegalStateException exception = expectThrows(IllegalStateException.class, () -> factory.createIngamePlayer(game, -1));
        assertEquals(exception.getMessage(), "Player id is negative");
    }

    @Test
    public void canStartGame() {
        Helpers.initializeLocalizer();
        LobbyPlayerAiSimpleFactory factory = new LobbyPlayerAiSimpleFactory("TestAI", null);
        Game game = Helpers.createGame();

        Player player = factory.createIngamePlayer(game, 0);
        assertNotNull(player);

        assertNotNull(player.getController());

    }

    @Test
    public void testSetRotateProfileEachGame() {
        LobbyPlayerAiSimpleFactory factory = new LobbyPlayerAiSimpleFactory("TestAI", null);
        factory.setRotateProfileEachGame(true);
        factory.setRotateProfileEachGame(false);
    }
}
