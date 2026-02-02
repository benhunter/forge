package forge.ai.common;

import forge.LobbyPlayer;

/**
 * Common base class for AI lobby players.
 */
public class LobbyPlayerAi extends LobbyPlayer {
    public LobbyPlayerAi(String name) {
        super(name);
    }

    @Override
    public void hear(LobbyPlayer player, String message) { /* Local AI is deaf. */ }

    public void setRotateProfileEachGame(boolean rotateProfileEachGame) {
        // No-op by default.
    }

    public void setAiProfile(String profile) {
        // No-op by default.
    }
}
