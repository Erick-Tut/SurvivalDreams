package dev.wux.survivaldreams.night;

public enum NightCyclePhase {
    NOCHE_TEMPRANA(0.00f, 0.35f),
    NOCHE(0.35f, 0.55f),
    MEDIANOCHE(0.55f, 0.80f),
    NOCHE_PROFUNDA(0.80f, 0.92f),
    NOCHE_TARDIA(0.92f, 1.00f);

    public final float from;
    public final float to;

    NightCyclePhase(float from, float to) {
        this.from = from;
        this.to = to;
    }

    public static NightCyclePhase fromProgress(float progress) {
        for (NightCyclePhase phase : values()) {
            if (progress >= phase.from && progress < phase.to) {
                return phase;
            }
        }
        return NOCHE_TARDIA;
    }
}