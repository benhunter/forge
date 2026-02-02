package forge.ai.neural;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NeuralDecisionEngine {
    private final NeuralModel model;
    private final NeuralStateEncoder encoder;
    private final Random random;
    private float temperature = 1.0f;

    public NeuralDecisionEngine(NeuralModel model, NeuralStateEncoder encoder, Random random) {
        this.model = Objects.requireNonNull(model, "model");
        this.encoder = Objects.requireNonNull(encoder, "encoder");
        this.random = Objects.requireNonNull(random, "random");
    }

    public void setTemperature(float temperature) {
        this.temperature = Math.max(0.05f, temperature);
    }

    public int chooseActionIndex(NeuralState state, NeuralActionSpace actionSpace) {
        NeuralPolicyOutput output = model.predict(state, actionSpace);
        float[] logits = output.getLogits();
        if (logits.length != actionSpace.size()) {
            logits = new float[actionSpace.size()];
        }
        return sampleSoftmax(logits);
    }

    public int chooseUniformly(int actionCount) {
        return random.nextInt(Math.max(actionCount, 1));
    }

    public NeuralState encodeState(forge.game.Game game, forge.game.player.Player player) {
        return encoder.encode(game, player);
    }

    private int sampleSoftmax(float[] logits) {
        double max = Double.NEGATIVE_INFINITY;
        for (float logit : logits) {
            max = Math.max(max, logit);
        }
        double sum = 0;
        double[] probs = new double[logits.length];
        for (int i = 0; i < logits.length; i++) {
            double scaled = (logits[i] - max) / temperature;
            probs[i] = Math.exp(scaled);
            sum += probs[i];
        }
        if (sum == 0) {
            return chooseUniformly(logits.length);
        }
        double r = random.nextDouble() * sum;
        double running = 0;
        for (int i = 0; i < probs.length; i++) {
            running += probs[i];
            if (r <= running) {
                return i;
            }
        }
        return logits.length - 1;
    }

    public <T> T chooseOption(List<T> options, NeuralState state, NeuralActionSpace actionSpace) {
        if (options.isEmpty()) {
            return null;
        }
        int idx = chooseActionIndex(state, actionSpace);
        return options.get(Math.min(idx, options.size() - 1));
    }
}
