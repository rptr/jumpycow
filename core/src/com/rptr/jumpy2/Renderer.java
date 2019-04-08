package com.rptr.jumpy2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.compression.lzma.Base;

import javax.xml.soap.Text;
import java.awt.peer.PanelPeer;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by rpj on 7/25/17.
 */
public class Renderer {
    public static int SCREEN_WIDTH = 480;
    public static int SCREEN_HEIGHT = 800;
    public static int POPUP_LIFETIME = 500;

    private Game game;
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont fontSmall;

    private Texture img;
    private Animation<TextureRegion> cowStand;
    private Animation<TextureRegion> cowWalk;
    private Animation<TextureRegion> cowFall;

    // enemies
    private Animation<TextureRegion> rocket;

    // platforms
    private TextureRegion tileGrass;
    private TextureRegion tileIce;
    private TextureRegion tileRoof;
    private TextureRegion tileWall;
    private Animation<TextureRegion> tileWater;
    private Animation<TextureRegion> tileQuicksand;

    // items
    private TextureRegion itemBigBottle;
    private TextureRegion itemSmallBottle;
    private TextureRegion itemMilkCan;
    private TextureRegion itemBigCarton;

    private TextureRegion itemSmallCarton;
    private TextureRegion itemCowSpawner;
    private TextureRegion itemBigCartonGreen;
    private TextureRegion itemBigCartonRed;

    private TextureRegion itemSmallCartonGreen;
    private TextureRegion itemSmallCartonRed;
    private Animation<TextureRegion> itemBombSmall;
    private TextureRegion itemStopwatch;

    private Texture splash;

    private Animation<TextureRegion> itemGlove;

    // pause screen
    private Texture singlePixelTexture;

    private float waterAnim = 0;
    private float sandAnim = 0;
    private float dT = 0;

    // popups
    private ArrayList<Popup> popups = new ArrayList<Popup>();

    public Renderer (Game game)
    {
        this.game = game;

        cam = new OrthographicCamera();
        cam.setToOrtho(false,
                SCREEN_WIDTH,
                SCREEN_HEIGHT);

        batch = new SpriteBatch();
        img = new Texture("sprites.png");

        font = new BitmapFont(Gdx.files.internal("font0.fnt"),
                Gdx.files.internal("font0.png"), false);
        fontSmall = new BitmapFont(Gdx.files.internal("font1.fnt"),
                Gdx.files.internal("font1.png"), false);

        // setup graphics
        // 32x32
//        cowStand = new Animation<TextureRegion>(0.4f,
////                makeTex(0, 1, 32, 32),
//                makeTex(0, 0, 32, 32),
//                makeTex(1, 0, 32, 32));
//        cowWalk = new Animation<TextureRegion>(0.1f,
//                makeTex(0, 2, 32, 32),
//                makeTex(1, 2, 32, 32));

        // 64x64 pix
        cowStand = new Animation<TextureRegion>(0.4f,
//                makeTex(0, 1, 32, 32),
                makeTex(0, 0, 64, 64),
                makeTex(3, 0, 64, 64));
        cowWalk = new Animation<TextureRegion>(0.1f,
                makeTex(0, 2, 64, 64),
//                makeTex(3, 2, 64, 64),
                makeTex(3, 1, 64, 64)
//                makeTex(3, 2, 64, 64)
        );
        cowFall = new Animation<TextureRegion>(0.1f,
                makeTex(2, 1, 64, 64),
                makeTex(2, 2, 64, 64)
        );

        // enemies
        rocket = new Animation<TextureRegion>(0.1f,
                makeTex(0, 5, 64, 64),
                makeTex(1, 5, 64, 64)
        );

        // wall tiles
        tileWall = makeTex(8, 0, 16, 64);

        // floor tiles
        tileGrass = makeTex(1, 0, 64, 16);
        tileIce = makeTex(1, 1, 64, 16);
        tileRoof = makeTex(1, 2, 64, 16);

        tileWater = new Animation<TextureRegion>(0.1f,
                makeTex(1, 3, 64, 16),
                makeTex(1, 4, 64, 16),
                makeTex(1, 5, 64, 16),
                makeTex(1, 4, 64, 16));
        tileQuicksand = new Animation<TextureRegion>(0.f,
                makeTex(1, 6, 64, 16),
                makeTex(1, 7, 64, 16));

        // items
        itemBigBottle = makeTex(1, 3, 64, 64);
        itemSmallBottle = makeTex(2, 8, 32, 32);
        itemMilkCan = makeTex(0, 3, 64, 64);
        itemBigCarton = makeTex(2, 3, 64, 64);

        itemBigCartonGreen = makeTex(3, 3, 64, 64);
        itemBigCartonRed = makeTex(3, 4, 64, 64);
        itemSmallCarton = makeTex(3, 8, 32, 32);
        itemSmallCartonGreen = makeTex(4, 8, 32, 32);

        itemSmallCartonRed = makeTex(5, 8, 32, 32);
        itemCowSpawner = makeTex(0, 4, 64, 64);
        itemBombSmall = new Animation<TextureRegion>(0.05f,
                makeTex(6, 10, 32, 32),
                makeTex(7, 10, 32, 32));
        itemStopwatch = makeTex(2, 5, 64, 64);

        itemGlove = new Animation<TextureRegion>(0.05f,
                makeTex(2, 9, 32, 32),
                makeTex(3, 9, 32, 32));

        // shamelessly borrowed from stackoverflow
        Pixmap singlePixelPixmap = new Pixmap(1,
                1,
                Pixmap.Format.RGBA8888);
        singlePixelPixmap.setColor(1, 1, 1, 1);
        singlePixelPixmap.fill();
        PixmapTextureData textureData = new PixmapTextureData(
                singlePixelPixmap,
                Pixmap.Format.RGBA8888,
                false,
                false,
                true);
        singlePixelTexture = new Texture(textureData);
        singlePixelTexture.setFilter(Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest);

        splash = new Texture("splash.png");
    }

    public TextureRegion makeTex (int x, int y, int w, int h)
    {
        return new TextureRegion(img, x * w, y * h, w, h);
    }

    public void draw (float dT)
    {
        this.dT = dT;

        switch (game.app.state)
        {
            case RUNNING:
                running(false, false);
                break;
            case PRE_START:
                preStart();
                break;
            case GAME_OVER:
                running(true, true);
                break;
            case PAUSED:
                running(true, false);
                break;
        }
    }

    // score text
    public void popup (float x, float y, String msg)
    {
        popups.add(new Popup(x, y, msg, TimeUtils.millis()));
    }

    private void preStart()
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
        batch.setProjectionMatrix(cam.combined);

        batch.begin();

        font.draw(batch, "TAP SCREEN TO PLAY",
                SCREEN_WIDTH / 2,
                SCREEN_HEIGHT / 2,
                0,
                1,
                false);

        font.draw(batch, "Tilt to move",
                SCREEN_WIDTH / 2,
                SCREEN_HEIGHT / 2 - 50,
                0,
                1,
                false);

        font.draw(batch, "Shake to jump",
                SCREEN_WIDTH / 2,
                SCREEN_HEIGHT / 2 - 100,
                0,
                1,
                false);

        batch.draw(splash, 25, SCREEN_HEIGHT - 420);

        batch.end();
    }

    private void running (boolean paused, boolean gameOver)
    {
        waterAnim += dT;
        sandAnim += dT;

        Gdx.gl.glClearColor(0.7f, 0.8f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
        batch.setProjectionMatrix(cam.combined);

        batch.begin();

        drawItems();
        drawCows();
        drawPlatforms();

        // draw extra milk

        int extraMilk = Game.getWaterLevel(game.score);
        batch.setColor(1, 1, 0.85f, 1);
        batch.draw(singlePixelTexture, 0, 0,
                SCREEN_WIDTH, extraMilk + 1);
        batch.setColor(1, 1, 1, 1);


        // isn't this why i originally had separate "paused" funcs?
        if (paused)
        {
            batch.setColor(0, 0, 0, 0.7f);
            batch.draw(singlePixelTexture, 0, 0,
                    SCREEN_WIDTH, SCREEN_HEIGHT);
            batch.setColor(1, 1, 1, 1);

            if (gameOver)
            {
                // XXX so much indentation
                // new high score!
                if (game.app.highscore.madeNewRecord)
                {
                    font.draw(batch,
                            "New high score!",
                            SCREEN_WIDTH / 2,
                            SCREEN_HEIGHT - 100,
                            0,
                            1,
                            false);
                }

                font.draw(batch,
                        "GAME OVER",
                        SCREEN_WIDTH / 2,
                        SCREEN_HEIGHT / 2,
                        0,
                        1,
                        false);

                font.draw(batch, "TAP TO PLAY AGAIN",
                        SCREEN_WIDTH / 2,
                        SCREEN_HEIGHT / 2 - 100,
                        0,
                        1,
                        false);
            } else
            {

                font.draw(batch, "TAP TO RESUME",
                        SCREEN_WIDTH / 2,
                        SCREEN_HEIGHT / 2,
                        0,
                        1,
                        false);
            }

            // PAUSED/ GAME OVER score
            font.draw(batch, Integer.toString((int)game.score) + " centilitres",
                    SCREEN_WIDTH / 2,
                    SCREEN_HEIGHT - 200,
                    0,
                    1,
                    false);

        // IF RUNNING
        } else
        {
            font.draw(batch, Integer.toString((int)game.score) + " cl",
                    SCREEN_WIDTH / 2,
                    SCREEN_HEIGHT - 40,
                    0,
                    1,
                    false);

            drawPopups();
        }

        batch.end();
    }

    private void drawCows ()
    {
        for (Cow c : game.getCows())
        {
            Animation<TextureRegion> anim = cowStand;
            boolean loop = true;

            if (Math.abs(c.velocity.x) > 5.0f)
                anim = cowWalk;

            if (c.velocity.y < 0)
            {
                anim = cowFall;
                loop = false;
            }

            if (c.isDrowning)
            {
                anim = cowWalk;
            }

            TextureRegion frame = anim.getKeyFrame(
                    c.getAnimState(dT),
                    loop);
            boolean flip = (c.direction == 1);

            drawCow(frame, c.position.x, c.position.y, c, flip);
        }
    }

    private void drawItems ()
    {
        ArrayList<Item> items = game.getItems();

        for (int i = 0; i < items.size(); i ++)
        {
            Item item = items.get(i);
            item.bob(dT);
            drawOne(itemTypeToTex(item.getType()),
                    item.position.x,
                    item.position.y + item.floatAnim,
                    item, (item.flyDir == 1));
        }
    }

    private void drawPlatforms ()
    {
        ArrayList<Platform> platforms = game.getPlatforms();

        // back to front, so that static platforms are drawn
        // last and on top
        for (int i = platforms.size() - 1; i >= 0; i --)
        {
            Platform p = platforms.get(i);
            drawOne(tileTypeToTile(p.type), p.position.x, p.position.y, p,
                    false);
        }
    }

    private void drawPopups ()
    {
        Iterator<Popup> popupIterator = popups.iterator();

        long time = TimeUtils.millis();

        while (popupIterator.hasNext())
        {
            Popup pop = popupIterator.next();

            pop.run(dT);
            float fade = (time - pop.birthTime) / POPUP_LIFETIME;
//            font.setColor(1, 1, 0, 1 - fade);
            fontSmall.draw(batch, pop.text, pop.x, pop.y);
//            font.setColor(1, 1, 1, 1);

            if (time >= pop.birthTime + POPUP_LIFETIME)
                popupIterator.remove();
        }
    }

    private TextureRegion tileTypeToTile (TileType type)
    {
        if (Platform.GRASS == type)
            return tileGrass;
        if (Platform.ICE == type)
            return tileIce;
        if (Platform.ROOF == type)
            return tileRoof;
        if (Platform.WALL == type)
            return tileWall;
        if (Platform.WATER == type)
            return tileWater.getKeyFrame(waterAnim, true);
        if (Platform.QUICKSAND == type)
            return tileQuicksand.getKeyFrame(sandAnim, true);

        // XXX should return some empty crap
        return tileWall;
    }

    private TextureRegion itemTypeToTex (ItemType type)
    {
        if (Item.CARTON_SMALL == type)
            return itemSmallCarton;
        if (Item.CARTON_SMALL_GREEN == type)
            return itemSmallCartonGreen;
        if (Item.CARTON_SMALL_RED == type)
            return itemSmallCartonRed;

        if (Item.CARTON_BIG == type)
            return itemBigCarton;
        if (Item.CARTON_BIG_GREEN == type)
            return itemBigCartonGreen;
        if (Item.CARTON_BIG_RED == type)
            return itemBigCartonRed;

        if (Item.MILK_CAN == type)
            return itemMilkCan;

        if (Item.BOTTLE_BIG == type)
            return itemBigBottle;
        if (Item.BOTTLE_SMALL == type)
            return itemSmallBottle;

        if (Item.COW_SPAWNER == type)
            return itemCowSpawner;

        if (Item.BOMB_SMALL == type)
            return itemBombSmall.getKeyFrame(waterAnim);
        if (Item.STOPWATCH == type)
            return itemStopwatch;

        if (Item.GLOVE == type)
            return itemGlove.getKeyFrame(waterAnim);

        // XXX should return some empty crap
        return tileWall;
    }

    // i am literally too fucking lazy to turn these two methods into one
    private void drawOne (TextureRegion r, float x, float y, BaseEntity ent,
                          boolean flip)
    {
        int screenW = Renderer.SCREEN_WIDTH;

        if (flip && !r.isFlipX())
            r.flip(true, false);
        else if (!flip && r.isFlipX())
            r.flip(true, false);

        batch.draw(r, (int) x, (int) y);

        // wrap around
//        if (x < 0)
//        {
//            batch.draw(r, (int) x + screenW, (int) y);
//
//        } else if (x > screenW - ent.width)
//        {
//            batch.draw(r, (int) x - screenW, (int) y);
//        }
    }

    // like drawOne except this one does squeezing
    private void drawCow (TextureRegion r, float x, float y, BaseEntity ent,
                          boolean flip)
    {
        int screenW = Renderer.SCREEN_WIDTH;
        int width = r.getRegionWidth();

        if (flip && !r.isFlipX())
            r.flip(true, false);
        else if (!flip && r.isFlipX())
            r.flip(true, false);

        batch.draw(r, (int) x, (int) y, width, ent.height);

        // wrap around
        if (x < 0)
        {
            batch.draw(r, (int) x + screenW, (int) y, width, ent.height);

        } else if (x > screenW - ent.width)
        {
            batch.draw(r, (int) x - screenW, (int) y, width, ent.height);
        }
    }

    void cleanup ()
    {
        batch.dispose();
        img.dispose();
    }
}
