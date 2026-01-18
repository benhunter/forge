package forge.game.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public record ZoneStateDto(
        @SerializedName("schemaVersion") String schemaVersion,
        @SerializedName("playerId") int playerId,
        @SerializedName("zones") List<ZoneDto> zones
) {
}
