package com.fishafish;

/** Resultado de uma fase concluída. */
public class PhaseResult {
    public final float totalTime;        // tempo total na fase (s)
    public final float pullTime;         // do 1º ao último aperto (s)
    public final int presses;
    public final float pressesPerSecond; // cadência de apertos

    public PhaseResult(float totalTime, float pullTime, int presses) {
        this.totalTime = totalTime;
        this.pullTime = pullTime;
        this.presses = presses;
        // (n-1) intervalos entre n apertos
        this.pressesPerSecond = (pullTime > 0f && presses > 1) ? (presses - 1) / pullTime : 0f;
    }
}
