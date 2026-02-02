package forge.ai.neural.training;

import java.util.StringJoiner;

/**
 * One training sample: state features, target policy, and outcome value.
 */
public final class NeuralTrainingExample {
    private final float[] state;
    private final float[] policy;
    private final float value;
    private final String metadata;

    public NeuralTrainingExample(float[] state, float[] policy, float value, String metadata) {
        this.state = state;
        this.policy = policy;
        this.value = value;
        this.metadata = metadata == null ? "" : metadata;
    }

    public float[] getState() {
        return state;
    }

    public float[] getPolicy() {
        return policy;
    }

    public float getValue() {
        return value;
    }

    public String getMetadata() {
        return metadata;
    }

    public String toRecord() {
        return "state=" + joinFloats(state)
            + ";policy=" + joinFloats(policy)
            + ";value=" + value
            + ";meta=" + metadata.replace(";", ",");
    }

    private String joinFloats(float[] values) {
        StringJoiner joiner = new StringJoiner(",");
        for (float value : values) {
            joiner.add(Float.toString(value));
        }
        return joiner.toString();
    }
}
