package forge.ai.mcts;

import forge.game.Game;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;

import java.util.List;
import java.util.Random;

public interface MctsRolloutPolicy {
    SpellAbility chooseAction(Game game, Player aiPlayer, List<SpellAbility> actions, Random random);
}
