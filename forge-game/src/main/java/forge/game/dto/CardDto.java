package forge.game.dto;

import com.google.gson.annotations.SerializedName;

public record CardDto(
        @SerializedName("id") int id,
        @SerializedName("displayId") String displayId,
        @SerializedName("name") String name,
        @SerializedName("oracleName") String oracleName,
        @SerializedName("ownerId") Integer ownerId,
        @SerializedName("controllerId") Integer controllerId,
        @SerializedName("zone") String zone,
        @SerializedName("imageKey") String imageKey,
        @SerializedName("isFaceDown") boolean isFaceDown,
        @SerializedName("isTapped") boolean isTapped,
        @SerializedName("isToken") boolean isToken,
        @SerializedName("isAttacking") boolean isAttacking,
        @SerializedName("isBlocking") boolean isBlocking,
        @SerializedName("currentState") CardStateDto currentState
) {
}
