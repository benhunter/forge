package forge.ai.simple;

import forge.ai.PlayerControllerAi;
import forge.game.Game;
import forge.game.GameRules;
import forge.game.GameType;
import forge.game.Match;
import forge.game.player.Player;
import forge.game.player.PlayerController;
import forge.game.player.RegisteredPlayer;
import forge.util.Localizer;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

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
        initializeLocalizer();

        LobbyPlayerAiSimpleFactory factory = new LobbyPlayerAiSimpleFactory("TestAI", null);
        assertNotNull(factory);
        assertEquals(factory.getName(), "TestAI");

        Game game = createGame();

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
        Game game = createGame();

        IllegalStateException exception = expectThrows(IllegalStateException.class, () -> factory.createIngamePlayer(game, -1));
        assertEquals(exception.getMessage(), "Player id is negative");
    }

    private Game createGame() {
        // Create a dummy Game object to pass to the method
        List<RegisteredPlayer> players = new ArrayList<>();
        GameType gameType = GameType.Constructed;
        GameRules rules = new GameRules(gameType);
        Match match = new Match(rules, players, "Test Match");
        return new Game(players, rules, match);
    }

    @Test
    public void canStartGame() {
        initializeLocalizer();
        LobbyPlayerAiSimpleFactory factory = new LobbyPlayerAiSimpleFactory("TestAI", null);
        Game game = createGame();

        Player player = factory.createIngamePlayer(game, 0);
        assertNotNull(player);

        assertNotNull(player.getController());

    }

    private void initializeLocalizer() {
        Localizer localizer = Localizer.getInstance();
        localizer.initialize("en-US", "../forge-gui/res/languages/"); // TODO: remove bad hardcoded path
    }
}
