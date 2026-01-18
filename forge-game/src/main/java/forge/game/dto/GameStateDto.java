package forge.game.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public record GameStateDto(
        @SerializedName("schemaVersion") String schemaVersion,
        @SerializedName("id") int id,
        @SerializedName("title") String title,
        @SerializedName("gameType") String gameType,
        @SerializedName("isCommander") boolean isCommander,
        @SerializedName("turn") int turn,
        @SerializedName("phase") String phase,
        @SerializedName("playerTurnId") Integer playerTurnId,
        @SerializedName("planarPlayerId") Integer planarPlayerId,
        @SerializedName("stormCount") int stormCount,
        @SerializedName("isMulligan") boolean isMulligan,
        @SerializedName("isGameOver") boolean isGameOver,
        @SerializedName("isMatchOver") boolean isMatchOver,
        @SerializedName("winningPlayerName") String winningPlayerName,
        @SerializedName("winningTeam") int winningTeam,
        @SerializedName("players") List<PlayerStateDto> players,
        @SerializedName("stack") List<StackItemDto> stack
) {
}
