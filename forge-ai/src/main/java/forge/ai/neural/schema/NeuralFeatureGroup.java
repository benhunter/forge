package forge.ai.neural.schema;

/**
 * Describes a contiguous group of features in the neural state vector.
 */
public final class NeuralFeatureGroup {
    private final String name;
    private final String description;
    private final int offset;
    private final int length;

    public NeuralFeatureGroup(String name, String description, int offset, int length) {
        this.name = name;
        this.description = description;
        this.offset = offset;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }
}
