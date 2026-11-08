package com.fishafish;

import com.badlogic.gdx.graphics.Color;

/** As 3 fases. Ajuste dificuldade/aparência aqui. */
public enum Phase {
    //            nome            cenário                          peixe                       alvoX  escala espMin espMax apertos
    LAGO_PEQUENO("Lago Pequeno", new Color(1f, 1f, 1f, 1f),       new Color(1f, 1f, 1f, 1f),   300f, 1.5f, 2f, 4f, Config.PRESSES_TO_CATCH),
    LAGO_GRANDE ("Lago Grande",  new Color(0.85f, 0.95f, 1f, 1f), new Color(1f, 0.8f, 0.8f, 1f), 430f, 2.2f, 3f, 6f, Config.PRESSES_TO_CATCH),
    MAR         ("Mar",          new Color(0.6f, 0.78f, 1f, 1f),  new Color(0.7f, 0.85f, 1f, 1f), 560f, 3.0f, 3f, 7f, Config.PRESSES_TO_CATCH);

    public final String nome;
    public final Color sceneTint, fishTint;
    public final float targetX;          // onde o anzol cai (quanto maior, mais longe)
    public final float fishScale;        // tamanho do peixe
    public final float waitMin, waitMax; // espera aleatória até o peixe morder (s)
    public final int presses;            // apertos necessários

    Phase(String nome, Color sceneTint, Color fishTint, float targetX, float fishScale,
          float waitMin, float waitMax, int presses) {
        this.nome = nome;
        this.sceneTint = sceneTint;
        this.fishTint = fishTint;
        this.targetX = targetX;
        this.fishScale = fishScale;
        this.waitMin = waitMin;
        this.waitMax = waitMax;
        this.presses = presses;
    }
}
