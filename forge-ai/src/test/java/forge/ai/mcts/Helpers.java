package forge.ai.mcts;

import forge.game.Game;
import forge.game.GameRules;
import forge.game.GameType;
import forge.game.Match;
import forge.game.player.RegisteredPlayer;
import forge.util.Localizer;

import java.util.ArrayList;
import java.util.List;

public class Helpers {
    public static Game createGame() {
        List<RegisteredPlayer> players = new ArrayList<>();
        GameType gameType = GameType.Constructed;
        GameRules rules = new GameRules(gameType);
        Match match = new Match(rules, players, "Test Match");
        return new Game(players, rules, match);
    }

    static void initializeLocalizer() {
        Localizer localizer = Localizer.getInstance();
        localizer.initialize("en-US", "../forge-gui/res/languages/");
    }
}
