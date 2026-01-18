package forge.game.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public record StackItemDto(
        @SerializedName("id") int id,
        @SerializedName("key") String key,
        @SerializedName("text") String text,
        @SerializedName("sourceCard") CardDto sourceCard,
        @SerializedName("activatingPlayerId") Integer activatingPlayerId,
        @SerializedName("targetCardIds") List<Integer> targetCardIds,
        @SerializedName("targetPlayerIds") List<Integer> targetPlayerIds,
        @SerializedName("isAbility") boolean isAbility,
        @SerializedName("isOptionalTrigger") boolean isOptionalTrigger,
        @SerializedName("optionalCosts") String optionalCosts,
        @SerializedName("subInstance") StackItemDto subInstance
) {
}
