package forge.ai.neural;

import forge.LobbyPlayer;
import forge.ai.simple.PlayerControllerAiSimple;
import forge.game.Game;
import forge.game.card.Card;
import forge.game.GameObject;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.spellability.SpellAbilityStackInstance;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

public class PlayerControllerAiNeural extends PlayerControllerAiSimple {
    private final NeuralDecisionEngine decisionEngine;
    private final NeuralActionSpaceBuilder actionSpaceBuilder;

    public PlayerControllerAiNeural(Game game, Player player, LobbyPlayer lobbyPlayer) {
        this(game, player, lobbyPlayer, new RandomPolicyModel(), new SimpleStateEncoder(), new Random());
    }

    public PlayerControllerAiNeural(
        Game game,
        Player player,
        LobbyPlayer lobbyPlayer,
        NeuralModel model,
        NeuralStateEncoder encoder,
        Random random
    ) {
        super(game, player, lobbyPlayer);
        this.decisionEngine = new NeuralDecisionEngine(model, encoder, random);
        this.actionSpaceBuilder = new NeuralActionSpaceBuilder();
    }

    @Override
    public SpellAbility getAbilityToPlay(Card hostCard, List<SpellAbility> abilities, forge.util.ITriggerEvent triggerEvent) {
        if (abilities == null || abilities.isEmpty()) {
            return null;
        }
        NeuralState state = decisionEngine.encodeState(getGame(), getPlayer());
        NeuralActionSpace actionSpace = actionSpaceBuilder.fromSpellAbilities(abilities);
        int choice = decisionEngine.chooseActionIndex(state, actionSpace);
        return abilities.get(Math.min(choice, abilities.size() - 1));
    }

    @Override
    public Pair<SpellAbilityStackInstance, GameObject> chooseTarget(
        SpellAbility sa,
        List<Pair<SpellAbilityStackInstance, GameObject>> allTargets
    ) {
        if (allTargets == null || allTargets.isEmpty()) {
            return null;
        }
        NeuralState state = decisionEngine.encodeState(getGame(), getPlayer());
        NeuralActionSpace actionSpace = actionSpaceBuilder.fromTargets(allTargets);
        int choice = decisionEngine.chooseActionIndex(state, actionSpace);
        return allTargets.get(Math.min(choice, allTargets.size() - 1));
    }

    @Override
    public int chooseNumber(SpellAbility sa, String title, List<Integer> values, Player relatedPlayer) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        NeuralState state = decisionEngine.encodeState(getGame(), getPlayer());
        NeuralActionSpace actionSpace = actionSpaceBuilder.fromChoiceValues(NeuralActionType.CHOOSE_NUMBER, values, "number:");
        int choice = decisionEngine.chooseActionIndex(state, actionSpace);
        return values.get(Math.min(choice, values.size() - 1));
    }

    @Override
    public boolean chooseBinary(SpellAbility sa, String question, BinaryChoiceType kindOfChoice, Boolean defaultChoice) {
        List<Integer> values = List.of(0, 1);
        NeuralState state = decisionEngine.encodeState(getGame(), getPlayer());
        NeuralActionSpace actionSpace = actionSpaceBuilder.fromChoiceValues(NeuralActionType.CHOOSE_NUMBER, values, "binary:");
        int choice = decisionEngine.chooseActionIndex(state, actionSpace);
        return choice == 1;
    }

}
