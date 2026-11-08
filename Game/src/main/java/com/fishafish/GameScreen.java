package com.fishafish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private enum State { IDLE, CASTING, WAITING, PULLING, CAUGHT }

    // Layout do cenário (coordenadas virtuais 640x360, y para cima)
    private static final float PIER_Y = 196f, WATER_Y = 180f;
    private static final float PLAYER_X = 24f, PLAYER_SCALE = 3f;
    private static final float TIP_X = PLAYER_X + 93f, TIP_Y = PIER_Y + 70f; // ponta da vara
    private static final float CAST_TIME = 0.7f;
    private static final Color YELLOW = new Color(1f, 0.9f, 0.35f, 1f);
    private static final Color ORANGE = new Color(1f, 0.6f, 0.2f, 1f);

    private final FishAFishGame game;
    private final Phase phase;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(Config.W, Config.H, camera);

    private State state = State.IDLE;
    private float phaseTime;           // tempo total na fase (para quando o peixe é pescado)
    private float stateTime;           // tempo no estado atual
    private float waitTarget;          // quanto esperar até o peixe morder
    private float bobX, bobY;          // posição da boia
    private int presses;
    private float firstPressTime = -1f, lastPressTime;
    private float shown;               // progresso suavizado (0..1) da puxada
    private PhaseResult result;

    public GameScreen(FishAFishGame game, Phase phase) {
        this.game = game;
        this.phase = phase;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void setState(State s) {
        state = s;
        stateTime = 0f;
    }

    // ------------------------------------------------------------------ lógica
    private void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        if (state != State.CAUGHT) phaseTime += dt;
        stateTime += dt;

        switch (state) {
            case IDLE:
                if (Gdx.input.isKeyJustPressed(Config.CAST_KEY)) setState(State.CASTING);
                break;
            case CASTING: {
                float k = Math.min(stateTime / CAST_TIME, 1f);
                bobX = MathUtils.lerp(TIP_X, phase.targetX, k);
                bobY = MathUtils.lerp(TIP_Y, WATER_Y, k) + MathUtils.sin(k * MathUtils.PI) * 80f;
                if (k >= 1f) {
                    waitTarget = MathUtils.random(phase.waitMin, phase.waitMax);
                    setState(State.WAITING);
                }
                break;
            }
            case WAITING:
                bobX = phase.targetX;
                bobY = WATER_Y + MathUtils.sin(stateTime * 4f) * 2f;
                if (stateTime >= waitTarget) { // o peixe mordeu!
                    presses = 0;
                    firstPressTime = -1f;
                    shown = 0f;
                    setState(State.PULLING);
                }
                break;
            case PULLING:
                if (Gdx.input.isKeyJustPressed(Config.PEDAL_KEY)) registerPress();
                shown += (presses / (float) phase.presses - shown) * Math.min(1f, dt * 8f);
                break;
            case CAUGHT:
                shown += (1f - shown) * Math.min(1f, dt * 8f);
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    int next = phase.ordinal() + 1;
                    if (next < Phase.values().length) game.setScreen(new GameScreen(game, Phase.values()[next]));
                    else game.setScreen(new SummaryScreen(game));
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                    game.setScreen(new GameScreen(game, phase));
                }
                break;
        }
    }

    private void registerPress() {
        presses++;
        if (presses == 1) firstPressTime = phaseTime;
        lastPressTime = phaseTime;
        if (presses >= phase.presses) {
            result = new PhaseResult(phaseTime, lastPressTime - firstPressTime, presses);
            game.results[phase.ordinal()] = result;
            setState(State.CAUGHT);
        }
    }

    /** Apertos por segundo (do 1º ao último aperto registrado até agora). */
    private float currentPps() {
        if (state == State.CAUGHT) return result.pressesPerSecond;
        float t = lastPressTime - firstPressTime;
        return (presses >= 2 && t > 0f) ? (presses - 1) / t : 0f;
    }

    // ------------------------------------------------------------------ desenho
    private boolean fishVisible() {
        return state == State.PULLING || state == State.CAUGHT;
    }

    private float fishMouthX() {
        return MathUtils.lerp(phase.targetX, TIP_X + 25f, shown);
    }

    private float fishMouthY() {
        float y = MathUtils.lerp(WATER_Y - 40f, TIP_Y - 55f, shown);
        if (state == State.PULLING) y += MathUtils.sin(phaseTime * 25f) * 2f; // peixe se debatendo
        return y;
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.shapes.setProjectionMatrix(camera.combined);

        // 1) imagens
        game.batch.begin();
        game.batch.setColor(phase.sceneTint);
        game.batch.draw(game.scenery, 0f, 0f, Config.W, Config.H);
        game.batch.setColor(Color.WHITE);
        game.batch.draw(game.player, PLAYER_X, PIER_Y, 32f * PLAYER_SCALE, 32f * PLAYER_SCALE);
        if (fishVisible()) {
            float s = phase.fishScale, mx = fishMouthX(), my = fishMouthY();
            Color c = phase.fishTint;
            game.batch.setColor(c.r, c.g, c.b, my < WATER_Y ? 0.7f : 1f); // mais transparente debaixo d'água
            game.batch.draw(game.fish, mx - 4f * s, my - 8f * s, 32f * s, 16f * s);
            game.batch.setColor(Color.WHITE);
        }
        game.batch.end();

        // 2) linha, boia, barras e painéis
        drawShapes();

        // 3) textos
        game.batch.begin();
        drawTexts();
        game.batch.end();
    }

    private void drawShapes() {
        ShapeRenderer sr = game.shapes;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // linha de pesca + boia
        float hx, hy;
        switch (state) {
            case IDLE:
                hx = TIP_X + 4f;
                hy = TIP_Y - 40f;
                break;
            case CASTING:
            case WAITING:
                hx = bobX;
                hy = bobY;
                break;
            default:
                hx = fishMouthX();
                hy = fishMouthY();
        }
        sr.setColor(1f, 1f, 1f, 0.9f);
        sr.rectLine(TIP_X, TIP_Y, hx, hy, 1.5f);
        if (state == State.CASTING || state == State.WAITING) {
            sr.setColor(0.9f, 0.2f, 0.2f, 1f);
            sr.circle(hx, hy + 5f, 5f);
            sr.setColor(1f, 1f, 1f, 1f);
            sr.circle(hx, hy + 1f, 3f);
        } else if (state == State.IDLE) {
            sr.setColor(0.8f, 0.8f, 0.8f, 1f);
            sr.circle(hx, hy, 2.5f);
        }

        // HUD
        sr.setColor(0f, 0f, 0f, 0.5f);
        sr.rect(8f, Config.H - 98f, 210f, 90f);
        if (state == State.CAUGHT) {
            sr.rect(240f, 70f, 380f, 200f);
        } else {
            sr.rect(110f, 12f, 420f, 40f);
        }
        if (state == State.PULLING) { // barra de progresso
            sr.setColor(0f, 0f, 0f, 0.7f);
            sr.rect(220f, 58f, 200f, 12f);
            sr.setColor(0.35f, 0.85f, 0.4f, 1f);
            sr.rect(222f, 60f, 196f * MathUtils.clamp(shown, 0f, 1f), 8f);
        }
        sr.end();
    }

    private void drawTexts() {
        float top = Config.H;
        game.text(phase.nome, 18f, top - 14f, 1.2f, YELLOW, false);
        game.text(String.format("Tempo: %.1f s", phaseTime), 18f, top - 38f, 1f, Color.WHITE, false);
        game.text("Apertos: " + presses + "/" + phase.presses, 18f, top - 58f, 1f, Color.WHITE, false);
        game.text(String.format("Apertos/s: %.2f", currentPps()), 18f, top - 78f, 1f, Color.WHITE, false);

        String cast = Input.Keys.toString(Config.CAST_KEY).toUpperCase();
        String pedal = Input.Keys.toString(Config.PEDAL_KEY).toUpperCase();
        switch (state) {
            case IDLE:
                game.text("Aperte " + cast + " para lançar o anzol", 320f, 44f, 1.3f, Color.WHITE, true);
                break;
            case WAITING:
                game.text("Aguarde... o peixe está chegando", 320f, 44f, 1.3f, Color.WHITE, true);
                break;
            case PULLING:
                boolean blink = ((int) (phaseTime * 4f)) % 2 == 0;
                game.text("Peixe fisgado! Aperte " + pedal + "!", 320f, 44f, 1.3f, blink ? YELLOW : ORANGE, true);
                break;
            case CAUGHT: {
                float cx = 430f;
                game.text("Peixe pescado!", cx, 258f, 1.6f, YELLOW, true);
                game.text(String.format("Tempo na fase: %.1f s", result.totalTime), cx, 218f, 1.2f, Color.WHITE, true);
                game.text(String.format("Tempo puxando: %.1f s", result.pullTime), cx, 194f, 1.2f, Color.WHITE, true);
                game.text(String.format("Apertos por segundo: %.2f", result.pressesPerSecond), cx, 170f, 1.2f, Color.WHITE, true);
                game.text("Total de apertos: " + result.presses, cx, 146f, 1.2f, Color.WHITE, true);
                String next = phase.ordinal() + 1 < Phase.values().length ? "próxima fase" : "ver resumo";
                game.text("ENTER: " + next + "  |  R: repetir  |  ESC: menu", cx, 104f, 0.9f, Color.WHITE, true);
                break;
            }
            default:
                break;
        }
    }
}
