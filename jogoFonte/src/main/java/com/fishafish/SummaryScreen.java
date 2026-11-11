package com.fishafish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** Resumo com o tempo e a cadência de cada fase. */
public class SummaryScreen extends ScreenAdapter {
    private static final Color YELLOW = new Color(1f, 0.9f, 0.35f, 1f);

    private final FishAFishGame game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(Config.W, Config.H, camera);

    public SummaryScreen(FishAFishGame game) {
        this.game = game;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.shapes.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(game.scenery, 0f, 0f, Config.W, Config.H);
        game.batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(0f, 0f, 0f, 0.6f);
        game.shapes.rect(50f, 50f, 540f, 260f);
        game.shapes.end();

        game.batch.begin();
        game.text("Parabéns! Todas as fases concluídas!", 320f, 296f, 1.5f, YELLOW, true);
        float[] cols = {80f, 250f, 390f, 500f};
        String[] heads = {"Fase", "Tempo na fase", "Puxando", "Apertos/s"};
        for (int c = 0; c < cols.length; c++) game.text(heads[c], cols[c], 240f, 1f, YELLOW, false);
        Phase[] phases = Phase.values();
        for (int i = 0; i < phases.length; i++) {
            float y = 205f - i * 34f;
            PhaseResult r = game.results[i];
            game.text(phases[i].nome, cols[0], y, 1.1f, Color.WHITE, false);
            game.text(r == null ? "-" : String.format("%.1f s", r.totalTime), cols[1], y, 1.1f, Color.WHITE, false);
            game.text(r == null ? "-" : String.format("%.1f s", r.pullTime), cols[2], y, 1.1f, Color.WHITE, false);
            game.text(r == null ? "-" : String.format("%.2f", r.pressesPerSecond), cols[3], y, 1.1f, Color.WHITE, false);
        }
        game.text("ENTER: voltar ao menu", 320f, 90f, 1f, Color.WHITE, true);
        game.batch.end();
    }
}
