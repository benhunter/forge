package forge.game.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public record ZoneDto(
        @SerializedName("zoneType") String zoneType,
        @SerializedName("size") int size,
        @SerializedName("cards") List<CardDto> cards
) {
}
