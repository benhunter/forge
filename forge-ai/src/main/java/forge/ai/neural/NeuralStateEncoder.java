package forge.ai.neural;

import forge.game.Game;
import forge.game.player.Player;

public interface NeuralStateEncoder {
    NeuralState encode(Game game, Player perspective);
}
