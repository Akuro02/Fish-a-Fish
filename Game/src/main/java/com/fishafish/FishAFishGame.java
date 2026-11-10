package com.fishafish;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class FishAFishGame extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public BitmapFont font;
    public Texture player, fish, scenery, menu, button;
    /** Último resultado de cada fase (null = ainda não jogada). */
    public final PhaseResult[] results = new PhaseResult[Phase.values().length];

    private final GlyphLayout layout = new GlyphLayout();

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = new BitmapFont();
        player = load("player.png");
        fish = load("fish.png");
        scenery = load("scenery.png");
        menu = load("menu.png");
        button = load("button.png");
        setScreen(new MenuScreen(this));
    }

    private Texture load(String file) {
        Texture t = new Texture(Gdx.files.internal(file));
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return t;
    }


    public void text(String s, float x, float y, float scale, Color c, boolean center) {
        font.getData().setScale(scale);
        layout.setText(font, s);
        float px = center ? x - layout.width / 2f : x;
        font.setColor(0f, 0f, 0f, 0.6f);
        font.draw(batch, s, px + 1f, y - 1f);
        font.setColor(c);
        font.draw(batch, s, px, y);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        shapes.dispose();
        font.dispose();
        player.dispose();
        fish.dispose();
        scenery.dispose();
        menu.dispose();
        button.dispose();
    }
}
