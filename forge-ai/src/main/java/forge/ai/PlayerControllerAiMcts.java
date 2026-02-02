package forge.ai;

import forge.LobbyPlayer;
import forge.ai.mcts.MctsSearch;
import forge.game.Game;
import forge.game.player.Player;

public class PlayerControllerAiMcts extends PlayerControllerAi {
    private int mctsMaxIterations = 200;
    private long mctsTimeLimitMs = 200;
    private double mctsExplorationConstant = Math.sqrt(2.0);

    public PlayerControllerAiMcts(Game game, Player p, LobbyPlayer lp) {
        super(game, p, lp);
    }

    public int getMctsMaxIterations() {
        return mctsMaxIterations;
    }

    public void setMctsMaxIterations(int mctsMaxIterations) {
        this.mctsMaxIterations = mctsMaxIterations;
    }

    public long getMctsTimeLimitMs() {
        return mctsTimeLimitMs;
    }

    public void setMctsTimeLimitMs(long mctsTimeLimitMs) {
        this.mctsTimeLimitMs = mctsTimeLimitMs;
    }

    public double getMctsExplorationConstant() {
        return mctsExplorationConstant;
    }

    public void setMctsExplorationConstant(double mctsExplorationConstant) {
        this.mctsExplorationConstant = mctsExplorationConstant;
    }

    public MctsSearch createMctsSearch() {
        MctsSearch search = new MctsSearch();
        search.setMaxIterations(mctsMaxIterations);
        search.setTimeLimitMs(mctsTimeLimitMs);
        search.setExplorationConstant(mctsExplorationConstant);
        return search;
    }
}
