package forge.ai.neural.training;

public class NeuralTrainingConfig {
    private int episodesPerIteration = 32;
    private int replayBufferSize = 10000;
    private int batchSize = 256;
    private float discountFactor = 0.99f;

    public int getEpisodesPerIteration() {
        return episodesPerIteration;
    }

    public void setEpisodesPerIteration(int episodesPerIteration) {
        this.episodesPerIteration = episodesPerIteration;
    }

    public int getReplayBufferSize() {
        return replayBufferSize;
    }

    public void setReplayBufferSize(int replayBufferSize) {
        this.replayBufferSize = replayBufferSize;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public float getDiscountFactor() {
        return discountFactor;
    }

    public void setDiscountFactor(float discountFactor) {
        this.discountFactor = discountFactor;
    }
}
