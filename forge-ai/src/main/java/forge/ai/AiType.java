package forge.ai;

import java.util.Arrays;

public enum AiType {
    SIMPLE("Simple"),
    ADVANCED_FSM("Advanced (FSM)"),
    MCTS("MCTS"),
    NEURAL("Neural");

    private final String displayName;

    AiType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean matchesPreference(String value) {
        return value != null
                && (displayName.equalsIgnoreCase(value) || name().equalsIgnoreCase(value));
    }

    public static AiType fromPreference(String value) {
        if (value != null) {
            for (AiType type : values()) {
                if (type.matchesPreference(value)) {
                    return type;
                }
            }
        }
        return SIMPLE;
    }

    public static boolean isKnownPreference(String value) {
        if (value == null) {
            return false;
        }
        return Arrays.stream(values()).anyMatch(type -> type.matchesPreference(value));
    }

    public static String[] getDisplayNames() {
        return Arrays.stream(values())
                .map(AiType::getDisplayName)
                .toArray(String[]::new);
    }
}
