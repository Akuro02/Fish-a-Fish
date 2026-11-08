package com.fishafish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen extends ScreenAdapter {
    private static final String[] LABELS = {"Lago Pequeno", "Lago Grande", "Mar", "Sair"};
    private static final float BTN_W = 192f, BTN_H = 48f, GAP = 10f, BTN_X = 372f, BTN_Y0 = 214f;
    private static final Color YELLOW = new Color(1f, 0.9f, 0.35f, 1f);
    private static final Color DIM = new Color(0.78f, 0.78f, 0.78f, 1f);

    private final FishAFishGame game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(Config.W, Config.H, camera);
    private final Vector2 mouse = new Vector2();
    private int selected = 0;
    private float lastMx = -1f, lastMy = -1f;

    public MenuScreen(FishAFishGame game) {
        this.game = game;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private float btnY(int i) {
        return BTN_Y0 - i * (BTN_H + GAP);
    }

    private int buttonAt(Vector2 p) {
        for (int i = 0; i < LABELS.length; i++) {
            if (p.x >= BTN_X && p.x <= BTN_X + BTN_W && p.y >= btnY(i) && p.y <= btnY(i) + BTN_H) return i;
        }
        return -1;
    }

    private void start(int i) {
        if (i < Phase.values().length) game.setScreen(new GameScreen(game, Phase.values()[i]));
        else Gdx.app.exit();
    }

    private void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { Gdx.app.exit(); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S))
            selected = (selected + 1) % LABELS.length;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W))
            selected = (selected + LABELS.length - 1) % LABELS.length;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            start(selected);
            return;
        }
        float mx = Gdx.input.getX(), my = Gdx.input.getY();
        if (mx != lastMx || my != lastMy) { // só muda a seleção se o mouse realmente moveu
            lastMx = mx;
            lastMy = my;
            int hit = buttonAt(viewport.unproject(mouse.set(mx, my)));
            if (hit >= 0) selected = hit;
        }
        if (Gdx.input.justTouched()) {
            int hit = buttonAt(viewport.unproject(mouse.set(Gdx.input.getX(), Gdx.input.getY())));
            if (hit >= 0) start(hit);
        }
    }

    @Override
    public void render(float delta) {
        update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(game.menu, 0f, 0f, Config.W, Config.H);
        for (int i = 0; i < LABELS.length; i++) {
            boolean sel = i == selected;
            float x = BTN_X + (sel ? -8f : 0f), y = btnY(i);
            game.batch.setColor(sel ? Color.WHITE : DIM);
            game.batch.draw(game.button, x, y, BTN_W, BTN_H);
            game.batch.setColor(Color.WHITE);
            game.text(LABELS[i], x + BTN_W / 2f, y + BTN_H / 2f + 10f, 1.3f, sel ? YELLOW : Color.WHITE, true);
        }
        game.text("Reabilitação com cicloergômetro", 468f, 290f, 1f, Color.WHITE, true);
        game.text("Setas + ENTER ou mouse  |  ESC sai", 468f, 28f, 0.9f, Color.WHITE, true);
        game.batch.end();
    }
}
