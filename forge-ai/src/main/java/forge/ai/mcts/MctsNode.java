package forge.ai.mcts;

import forge.game.Game;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;

import java.util.ArrayList;
import java.util.List;

public class MctsNode {
    private final MctsNode parent;
    private final SpellAbility actionFromParent;
    private final Game game;
    private final Player aiPlayer;
    private final List<SpellAbility> decisionContext;
    private final List<MctsNode> children = new ArrayList<>();
    private final List<SpellAbility> untriedActions;
    private int visitCount;
    private double totalScore;

    public MctsNode(MctsNode parent, SpellAbility actionFromParent, Game game, Player aiPlayer, List<SpellAbility> decisionContext) {
        this.parent = parent;
        this.actionFromParent = actionFromParent;
        this.game = game;
        this.aiPlayer = aiPlayer;
        this.decisionContext = new ArrayList<>(decisionContext);
        this.untriedActions = new ArrayList<>(decisionContext);
    }

    public MctsNode getParent() {
        return parent;
    }

    public SpellAbility getActionFromParent() {
        return actionFromParent;
    }

    public Game getGame() {
        return game;
    }

    public Player getAiPlayer() {
        return aiPlayer;
    }

    public List<MctsNode> getChildren() {
        return children;
    }

    public List<SpellAbility> getDecisionContext() {
        return decisionContext;
    }

    public boolean isFullyExpanded() {
        return untriedActions.isEmpty();
    }

    public boolean isTerminal() {
        return game.isGameOver() || decisionContext.isEmpty();
    }

    public SpellAbility pollUntriedAction() {
        if (untriedActions.isEmpty()) {
            return null;
        }
        return untriedActions.remove(untriedActions.size() - 1);
    }

    public void addChild(MctsNode child) {
        children.add(child);
    }

    public int getVisitCount() {
        return visitCount;
    }

    public double getMeanScore() {
        return visitCount == 0 ? 0.0 : totalScore / visitCount;
    }

    public void updateStats(double score) {
        visitCount++;
        totalScore += score;
    }
}
