package forge.game.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public record StackStateDto(
        @SerializedName("schemaVersion") String schemaVersion,
        @SerializedName("gameId") int gameId,
        @SerializedName("items") List<StackItemDto> items
) {
}
