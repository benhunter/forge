package forge.ai.neural;

import forge.game.Game;
import forge.game.phase.PhaseType;
import forge.game.player.Player;
import forge.game.zone.ZoneType;

import java.util.ArrayList;
import java.util.List;

public class SimpleStateEncoder implements NeuralStateEncoder {
    private static final float LIFE_NORM = 40f;
    private static final float ZONE_SIZE_NORM = 60f;
    private static final float BATTLEFIELD_NORM = 30f;
    private static final float MANA_NORM = 10f;

    @Override
    public NeuralState encode(Game game, Player perspective) {
        List<Float> values = new ArrayList<>();
        List<String> names = new ArrayList<>();
        PhaseType phase = game.getPhaseHandler().getPhase();

        addOneHot(values, names, "phase", phase != null ? phase.ordinal() : -1, PhaseType.values().length);
        addScalar(values, names, "turn", game.getPhaseHandler().getTurn() / 20f);
        addScalar(values, names, "active_player", game.getPhaseHandler().isPlayerTurn(perspective) ? 1f : 0f);

        encodePlayer(values, names, "self", perspective);
        for (Player opponent : perspective.getOpponents()) {
            encodePlayer(values, names, "opp", opponent);
        }

        float[] features = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            features[i] = values.get(i);
        }
        return new NeuralState(features, names);
    }

    private void encodePlayer(List<Float> values, List<String> names, String prefix, Player player) {
        addScalar(values, names, prefix + ".life", player.getLife() / LIFE_NORM);
        addScalar(values, names, prefix + ".poison", player.getPoisonCounters() / 10f);
        addScalar(values, names, prefix + ".hand", player.getCardsIn(ZoneType.Hand).size() / ZONE_SIZE_NORM);
        addScalar(values, names, prefix + ".library", player.getCardsIn(ZoneType.Library).size() / ZONE_SIZE_NORM);
        addScalar(values, names, prefix + ".graveyard", player.getCardsIn(ZoneType.Graveyard).size() / ZONE_SIZE_NORM);
        addScalar(values, names, prefix + ".exile", player.getCardsIn(ZoneType.Exile).size() / ZONE_SIZE_NORM);
        addScalar(values, names, prefix + ".battlefield", player.getCardsIn(ZoneType.Battlefield).size() / BATTLEFIELD_NORM);
        addScalar(values, names, prefix + ".lands", player.getLandsInPlay().size() / BATTLEFIELD_NORM);
        addScalar(values, names, prefix + ".creatures", player.getCreaturesInPlay().size() / BATTLEFIELD_NORM);
        addScalar(values, names, prefix + ".mana_pool", player.getManaPool().totalMana() / MANA_NORM);
    }

    private void addScalar(List<Float> values, List<String> names, String name, float value) {
        values.add(value);
        names.add(name);
    }

    private void addOneHot(List<Float> values, List<String> names, String namePrefix, int index, int length) {
        for (int i = 0; i < length; i++) {
            values.add(i == index ? 1f : 0f);
            names.add(namePrefix + "." + i);
        }
    }
}
