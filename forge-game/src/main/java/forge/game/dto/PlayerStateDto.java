package forge.game.dto;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public record PlayerStateDto(
        @SerializedName("schemaVersion") String schemaVersion,
        @SerializedName("id") int id,
        @SerializedName("name") String name,
        @SerializedName("lobbyPlayerName") String lobbyPlayerName,
        @SerializedName("isAi") boolean isAi,
        @SerializedName("life") int life,
        @SerializedName("isExtraTurn") boolean isExtraTurn,
        @SerializedName("hasDelirium") boolean hasDelirium,
        @SerializedName("currentPlane") String currentPlane,
        @SerializedName("manaPool") Map<String, Integer> manaPool,
        @SerializedName("counters") Map<String, Integer> counters,
        @SerializedName("opponentIds") List<Integer> opponentIds,
        @SerializedName("zones") List<ZoneDto> zones
) {
}
