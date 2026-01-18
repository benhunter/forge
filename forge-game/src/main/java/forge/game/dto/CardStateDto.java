package forge.game.dto;

import com.google.gson.annotations.SerializedName;

public record CardStateDto(
        @SerializedName("state") String state,
        @SerializedName("name") String name,
        @SerializedName("oracleName") String oracleName,
        @SerializedName("types") String types,
        @SerializedName("manaCost") String manaCost,
        @SerializedName("power") Integer power,
        @SerializedName("toughness") Integer toughness,
        @SerializedName("loyalty") String loyalty,
        @SerializedName("oracleText") String oracleText,
        @SerializedName("rulesText") String rulesText
) {
}
