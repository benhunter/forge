package forge.ai.mcts;

import forge.ai.simulation.MultiTargetSelector;
import forge.game.Game;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MctsNode {
    private final Game game;
    private final Player player;
    private final List<SpellAbility> decisionContext;
    private final MultiTargetSelector.Targets targetContext;
    private final MctsNode parent;
    private final List<MctsNode> children;
    private final List<SpellAbility> untriedActions;
    private final SpellAbility incomingAction;
    private int visitCount;
    private double totalScore;

    public MctsNode(Game game,
                    Player player,
                    List<SpellAbility> decisionContext,
                    MultiTargetSelector.Targets targetContext,
                    MctsNode parent,
                    SpellAbility incomingAction) {
        this.game = game;
        this.player = player;
        this.decisionContext = decisionContext != null ? decisionContext : List.of();
        this.targetContext = targetContext;
        this.parent = parent;
        this.incomingAction = incomingAction;
        this.children = new ArrayList<>();
        this.untriedActions = new ArrayList<>(this.decisionContext);
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public List<SpellAbility> getDecisionContext() {
        return decisionContext;
    }

    public MultiTargetSelector.Targets getTargetContext() {
        return targetContext;
    }

    public MctsNode getParent() {
        return parent;
    }

    public List<MctsNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasUntriedActions() {
        return !untriedActions.isEmpty();
    }

    public boolean isFullyExpanded() {
        return untriedActions.isEmpty();
    }

    public SpellAbility popUntriedAction(Random random) {
        if (untriedActions.isEmpty()) {
            return null;
        }
        if (random != null && untriedActions.size() > 1) {
            return untriedActions.remove(random.nextInt(untriedActions.size()));
        }
        return untriedActions.remove(0);
    }

    public MctsNode addChild(SpellAbility action,
                             Game childGame,
                             Player childPlayer,
                             List<SpellAbility> childActions,
                             MultiTargetSelector.Targets targetContext) {
        MctsNode child = new MctsNode(childGame, childPlayer, childActions, targetContext, this, action);
        children.add(child);
        return child;
    }

    public SpellAbility getIncomingAction() {
        return incomingAction;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public double getMeanScore() {
        return visitCount == 0 ? 0.0 : totalScore / visitCount;
    }

    public void recordScore(double score) {
        visitCount += 1;
        totalScore += score;
    }

    public MctsNode selectChildUct(double explorationConstant) {
        MctsNode best = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        double logParentVisits = Math.log(Math.max(1, visitCount));
        for (MctsNode child : children) {
            if (child.visitCount == 0) {
                return child;
            }
            double exploitation = child.getMeanScore();
            double exploration = explorationConstant * Math.sqrt(logParentVisits / child.visitCount);
            double value = exploitation + exploration;
            if (value > bestValue) {
                bestValue = value;
                best = child;
            }
        }
        return best != null ? best : (children.isEmpty() ? null : children.get(0));
    }
}
