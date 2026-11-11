package com.fishafish;

import com.badlogic.gdx.Input;

/** Configurações centrais. Quando o cicloergômetro for integrado, troque as teclas por leitura do sensor. */
public final class Config {
    private Config() {}

    public static final float W = 640f, H = 360f;          // resolução virtual
    public static final int CAST_KEY = Input.Keys.ENTER;   // joga o anzol
    public static final int PEDAL_KEY = Input.Keys.SPACE;  // simula a pedalada / manivela
    public static final int[] PRESSES_TO_CATCH = {10, 20, 30};         // apertos para puxar o peixe
}
