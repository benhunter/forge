package forge.ai.neural;

import forge.LobbyPlayer;
import forge.ai.neural.training.NeuralTrainingDataCollector;
import forge.game.Game;
import forge.game.card.CardCollectionView;
import forge.game.player.Player;
import forge.game.spellability.AbilitySub;
import forge.game.spellability.SpellAbility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Neural-network based AI built on top of the simple AI controller.
 */
public class PlayerControllerAiNeural extends forge.ai.simple.PlayerControllerAiSimple {
    private final Game game;
    private final NeuralDecisionEngine decisionEngine;
    private final NeuralTrainingDataCollector dataCollector;

    public PlayerControllerAiNeural(Game game, Player player, LobbyPlayer lobbyPlayer) {
        this(game, player, lobbyPlayer, new RandomPolicyValueNetwork(), null);
    }

    public PlayerControllerAiNeural(Game game,
                                    Player player,
                                    LobbyPlayer lobbyPlayer,
                                    PolicyValueNetwork network,
                                    NeuralTrainingDataCollector dataCollector) {
        super(game, player, lobbyPlayer);
        this.game = game;
        this.decisionEngine = new NeuralDecisionEngine(new NeuralStateEncoder(), network);
        this.dataCollector = dataCollector;
    }

    @Override
    public List<SpellAbility> chooseSpellAbilitiesForEffect(List<SpellAbility> spells, SpellAbility sa, String title, int num, Map<String, Object> params) {
        if (spells == null || spells.isEmpty() || num <= 0) {
            return List.of();
        }
        SpellAbility choice = selectSpellAbility(spells, "chooseSpellAbilitiesForEffect");
        return choice == null ? List.of() : List.of(choice);
    }

    @Override
    public SpellAbility chooseSingleSpellForEffect(List<SpellAbility> spells, SpellAbility sa, String title, Map<String, Object> params) {
        return selectSpellAbility(spells, "chooseSingleSpellForEffect");
    }

    @Override
    public List<AbilitySub> chooseModeForAbility(SpellAbility sa, List<AbilitySub> possible, int min, int num, boolean allowRepeat) {
        if (possible == null || possible.isEmpty()) {
            return List.of();
        }
        AbilitySub choice = selectAbilityMode(possible, "chooseModeForAbility");
        return choice == null ? List.of() : List.of(choice);
    }

    @Override
    public int chooseNumber(SpellAbility sa, String title, List<Integer> values, Player relatedPlayer) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        return selectNumber(values, "chooseNumber");
    }

    @Override
    public boolean chooseBinary(SpellAbility sa, String question, BinaryChoiceType kindOfChoice, Boolean defaultChoice) {
        return selectBoolean("chooseBinary");
    }

    @Override
    public CardCollectionView choosePermanentsToSacrifice(SpellAbility sa, int min, int max, CardCollectionView validTargets, String message) {
        return super.choosePermanentsToSacrifice(sa, min, max, validTargets, message);
    }

    private SpellAbility selectSpellAbility(List<SpellAbility> spells, String context) {
        List<NeuralAction> actions = new ArrayList<>();
        for (SpellAbility ability : spells) {
            actions.add(NeuralAction.forSpellAbility(ability));
        }
        NeuralDecisionEngine.Decision decision = decisionEngine.selectAction(game, player, actions);
        recordDecision(decision, context);
        int selectedIndex = decision.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= spells.size()) {
            return spells.get(0);
        }
        return spells.get(selectedIndex);
    }

    private AbilitySub selectAbilityMode(List<AbilitySub> modes, String context) {
        List<NeuralAction> actions = new ArrayList<>();
        for (AbilitySub mode : modes) {
            actions.add(NeuralAction.forAbilityMode(mode));
        }
        NeuralDecisionEngine.Decision decision = decisionEngine.selectAction(game, player, actions);
        recordDecision(decision, context);
        int selectedIndex = decision.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= modes.size()) {
            return modes.get(0);
        }
        return modes.get(selectedIndex);
    }

    private int selectNumber(List<Integer> values, String context) {
        List<NeuralAction> actions = new ArrayList<>();
        for (Integer value : values) {
            actions.add(NeuralAction.forNumber(value));
        }
        NeuralDecisionEngine.Decision decision = decisionEngine.selectAction(game, player, actions);
        recordDecision(decision, context);
        int selectedIndex = decision.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= values.size()) {
            return values.get(0);
        }
        return values.get(selectedIndex);
    }

    private boolean selectBoolean(String context) {
        List<NeuralAction> actions = List.of(NeuralAction.forBoolean(true), NeuralAction.forBoolean(false));
        NeuralDecisionEngine.Decision decision = decisionEngine.selectAction(game, player, actions);
        recordDecision(decision, context);
        return decision.getSelectedIndex() == 0;
    }

    private void recordDecision(NeuralDecisionEngine.Decision decision, String context) {
        if (dataCollector != null) {
            dataCollector.recordDecision(decision, context);
        }
    }
}
