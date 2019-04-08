package com.rptr.jumpy2;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sun.corba.se.impl.ior.JIDLObjectKeyTemplate;

public class JumpyGame extends ApplicationAdapter {

    // deltaTime;
    float dT = 0;

    public enum Status {
        PRE_START,
        RUNNING,
        PAUSED,
        GAME_OVER
    }

	public Status state = Status.PRE_START;
	public Game game;
	public Renderer renderer;
	public HighScore highscore;

    public int adHeight = 0;

	private void run ()
    {
        switch (state) {
            case PRE_START:
                preStart();
                break;
            case RUNNING:
                running();
                break;
            case PAUSED:
                paused();
                break;
            case GAME_OVER:
                gameOver();
                break;
        }
    }

    private void preStart ()
    {
        if (Gdx.input.justTouched())
        {
            game.restart();
        }
    }

    private void running ()
    {
        if (Gdx.input.justTouched())
        {
            state = Status.PAUSED;
            return;
        }

        game.run(dT);
    }

    public void loseGame ()
    {
        Jukebox.stopMusic();
        highscore.gameOver((int)game.score);
        state = Status.GAME_OVER;
    }

    private void paused ()
    {
        if (Gdx.input.justTouched())
        {
            state = Status.RUNNING;
        }
    }

    private void gameOver ()
    {
        if (Gdx.input.justTouched())
        {
            game.restart();
        }
    }

    public JumpyGame setAdHeight (int height)
    {
        adHeight = height;
        return this;
    }

	@Override
	public void create ()
    {
        // set up sounds
        Jukebox.moo1 = Gdx.audio.newSound(Gdx.files.internal("moo_1.ogg"));
        Jukebox.moo2 = Gdx.audio.newSound(Gdx.files.internal("moo_2.ogg"));
        Jukebox.moo3 = Gdx.audio.newSound(Gdx.files.internal("moo_3.ogg"));
        Jukebox.moo4 = Gdx.audio.newSound(Gdx.files.internal("moo_4.ogg"));
        Jukebox.moo5 = Gdx.audio.newSound(Gdx.files.internal("moo_5.ogg"));

        Jukebox.cowDeath =
                Gdx.audio.newSound(Gdx.files.internal("cow_death.ogg"));

        Jukebox.pling1 = Gdx.audio.newSound(Gdx.files.internal("a1.ogg"));
        Jukebox.pling2 = Gdx.audio.newSound(Gdx.files.internal("a2.ogg"));
        Jukebox.pling3 = Gdx.audio.newSound(Gdx.files.internal("a3.ogg"));

        Jukebox.jump1 = Gdx.audio.newSound(Gdx.files.internal("jump_1.ogg"));
        Jukebox.jump2 = Gdx.audio.newSound(Gdx.files.internal("jump_2.ogg"));
        Jukebox.jump3 = Gdx.audio.newSound(Gdx.files.internal("jump_3.ogg"));

        Jukebox.explosion =
                Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        Jukebox.thud =
                Gdx.audio.newSound(Gdx.files.internal("thud.ogg"));
        Jukebox.splash =
                Gdx.audio.newSound(Gdx.files.internal("splash.ogg"));
        Jukebox.pour = Gdx.audio.newSound(Gdx.files.internal("pour.ogg"));

        Jukebox.music =
                Gdx.audio.newMusic(Gdx.files.internal("chibi_ninja.ogg"));

        Jukebox.clock = Gdx.audio.newSound(Gdx.files.internal("clock.ogg"));
        Jukebox.rocket = Gdx.audio.newSound(Gdx.files.internal("rocket.ogg"));

        Jukebox.bif1 = Gdx.audio.newSound(Gdx.files.internal("bif1.ogg"));
        Jukebox.bif2 = Gdx.audio.newSound(Gdx.files.internal("bif2.ogg"));

        // tiles
        Platform.GRASS = new TileType(64, 16, true, false);
        Platform.ICE = new TileType(64, 16, true, false);
        Platform.QUICKSAND =
                new TileType(64, 16, true, false);
        // static
        Platform.WATER = new TileType(64, 16, true, true);
        Platform.ROOF = new TileType(64, 16, true, true);
        // walls
        Platform.WALL = new TileType(16, 64, true, false);

        Item.BOTTLE_BIG = new ItemType(34, 40, true, 100,
                15, 12, 1);
        Item.BOTTLE_SMALL = new ItemType(16, 16, true, 20,
                8, 8, 0.5f);
        Item.MILK_CAN = new ItemType(34, 40, true, 333,
                15, 12, 1.3f);

        Item.CARTON_BIG = new ItemType(40, 48, true, 155,
                12, 8, 1);
        Item.CARTON_BIG_GREEN = new ItemType(40, 48, true, 205,
                12, 8, 1);
        Item.CARTON_BIG_RED = new ItemType(40, 48, true, 255,
                12, 8, 1);

        Item.CARTON_SMALL = new ItemType(18, 20, true, 25,
                7, 6, 1.5f);
        Item.CARTON_SMALL_GREEN = new ItemType(18, 20, true, 35,
                7, 6, 1.5f);
        Item.CARTON_SMALL_RED = new ItemType(18, 20, true, 45,
                7, 6, 1.5f);

        Item.COW_SPAWNER = new ItemType(54, 54, true, 0,
                5, 5, 1.0f);

        Item.BOMB_SMALL = new ItemType(28, 28, true, 0,
                4, 4, 1.0f);
        Item.STOPWATCH = new ItemType(48, 48, true, 0,
                8, 8, 1.5f);

        Item.GLOVE = new ItemType(32, 32, false, 0, 0, 0, 2);
        Item.GLOVE.doBob = false;

	    Gdx.app.setLogLevel(Application.LOG_DEBUG);

        state = Status.PRE_START;

	    highscore = new HighScore();
		game = new Game(this);
		renderer = new Renderer(game);
	}

	@Override
	public void render () {
        dT = Gdx.graphics.getDeltaTime();
	    run();
	    renderer.draw(dT);
	    Jukebox.run(dT);
	}

	@Override
	public void dispose () {
	    renderer.cleanup();
	}
}
