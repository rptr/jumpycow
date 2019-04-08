package com.rptr.jumpy2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.*;

/**
 * Created by rpj on 7/24/17.
 */

public class Game {
    public static float GRAVITY = 400.0f;
    public static float TERMINAL_VELOCITY = 400.0f;
    public static float SHAKE_ACCELERATION = 12.0f;
    public static int SCREEN_TILES_WIDE = 7;
    public static int LEVEL_HEIGHT_MIN = 100;
    public static int LEVEL_HEIGHT_MAX = 130;
    public static int ICE_CHANCE = 10;
    public static int COW_SPAWNER_CHANCE = 50;
    public static int BIG_ITEM_CHANCE = 14;
    public static int ITEMS_LINE_CHANCE = 3;
    public static int SAND_CHANCE = 10;
    static int STOPWATCH_CHANCE = 60;
    static int SMALL_BOMB_CHANCE = 35;
    static int SMALL_BOMB_EXPLOSION_DISTANCE = 100;
    static int WALL_EMPTY_CHANCE = 8;
    static float DEFAULT_SCROLL_SPEED = 100.0f;
    static float STOPWATCH_SPEED_UP_FACTOR = 2.0f;
    static int SPEED_UP_MAX_SECONDS = 4;
    static float SCORE_WATER_RATIO = 25.0f;
    static float MAX_WATER_LEVEL = Renderer.SCREEN_HEIGHT - 300;
    // per 0.1 seconds
    static int GLOVE_CHANCE = 30;

    public static RandomXS128 random;

    static private int minWaterHeight;

    // a in b, e.g. 1 in 5
    public static boolean randomChance (int a, int b)
    {
        return (random.nextInt(b) <= a - 1);
    }

    public static int getWaterLevel (int score)
    {
        return (int) Math.min(score / Game.SCORE_WATER_RATIO,
                Game.MAX_WATER_LEVEL) + minWaterHeight;
    }

    // just cause i\m lazy
    public static int getWaterLevel (float score)
    {
        return getWaterLevel((int)score);
    }

    private ArrayList<Platform> platforms = new ArrayList<Platform>();
    private ArrayList<Cow> cows = new ArrayList<Cow>();
    private ArrayList<Item> items = new ArrayList<Item>();

    // the speed at which the world scrolls past
    private float worldScrollSpeed = DEFAULT_SCROLL_SPEED;
    // how much the world has moved since the last platform spawn
    private float worldMovedY = 0;
    // vertical distance between platforms
    private float levelHeight = 150;
    private boolean canShake = true;
    private float wallsMovedY = 0;
    private float speedUpTimer = 0;
    private float gloveCount = 0;

    public float score = 0;

    public JumpyGame app;

    public Game (JumpyGame app)
    {
        this.app = app;
        minWaterHeight = app.adHeight;
        random  = new RandomXS128();
    }

    public void restart ()
    {
        cows.clear();
        platforms.clear();
        items.clear();

        int height = Renderer.SCREEN_HEIGHT;
        int width = Renderer.SCREEN_WIDTH;

        score = 0;

        // make static level
        // water + roof
        for (int i = 0; i < 7; i ++)
        {
            platforms.add(
                    Platform.make(i * 64, 0, Platform.WATER));
            platforms.add(
                    Platform.make(i * 64 + 16, height - 16, Platform.ROOF));
        }

        // extra water to cover bottom completely
        platforms.add(
                Platform.make(7 * 64, 0, Platform.WATER));

        // walls
        for (int i = 0; i < 14; i ++)
        {
            if (randomChance(1, 10))
                continue;

            newWall(0, i * Platform.WIDTH - Platform.WIDTH);
            newWall(width - 16, i * Platform.WIDTH - Platform.WIDTH);
        }

        int h = 0;

        // initial platforms
        for (int i = 0; i < 3; i ++)
        {
            h += randomLevelHeight();
            makeNewLevel(h, true);
        }

        // add first cow!
        cows.add(Cow.make(width / 2 - 32, height / 2));

        app.state = JumpyGame.Status.RUNNING;

        Jukebox.startMusic();
    }

    public void run (float dT) {
        float aX = Gdx.input.getAccelerometerX();
        float tilt = -aX;
        float accY = Gdx.input.getAccelerometerY();
        boolean shake = false;
        float scoreMod = 1;

        if (speedUpTimer > 0)
        {
            worldScrollSpeed = DEFAULT_SCROLL_SPEED *
                    STOPWATCH_SPEED_UP_FACTOR;
            speedUpTimer -= dT;

            scoreMod = 5;

        } else
        {
            worldScrollSpeed = DEFAULT_SCROLL_SPEED;
        }

        // 1 pts per second
        score += dT * scoreMod;

        if (accY > SHAKE_ACCELERATION && canShake)
        {
            shake = true;
            canShake = false;
        }

        if (accY < SHAKE_ACCELERATION)
        {
            canShake = true;
        }

        newLevel(dT);

        Iterator<Platform> platformIterator = platforms.iterator();

        while (platformIterator.hasNext())
        {
            Platform p = platformIterator.next();

            p.scroll(dT, worldScrollSpeed, (int)score);

            if (p.killMe)
            {
                platformIterator.remove();
            }
        }

        Iterator<Cow> cowIterator = cows.iterator();

        while (cowIterator.hasNext())
        {
            Cow c = cowIterator.next();
            c.tilt = tilt;
            c.run(dT, platforms, shake, items, cows, this);

            if (c.killMe)
            {
                cowIterator.remove();
            }
        }

        // game over
        // if it's the last cow, let it sink a little
        if (cows.size() == 0 ||
            (cows.size() == 1 &&
            cows.get(0).position.y <=
                    getWaterLevel(score) - 16))
        {
            app.loseGame();
        }

        Iterator<Item> itemIterator = items.iterator();

        while (itemIterator.hasNext())
        {
            Item i = itemIterator.next();

            if (i.killMe)
            {
                itemIterator.remove();

                if (i.pickMeUp)
                    pickUpItem(i);

                continue;
            }

            i.scroll(dT, worldScrollSpeed, (int)score);
            i.run(dT);
        }

        gloveCount += dT;

        if (gloveCount >= 0.1f)
        {
            Cow cow = cows.get(random.nextInt(cows.size()));

            // shoot some gloves
            if (randomChance(1, GLOVE_CHANCE))
            {
                int[] sides = {-32, Renderer.SCREEN_WIDTH};
                int side = sides[random.nextInt(2)];

                float gloveY = cow.position.y;
                addItem(side, gloveY, Item.GLOVE);
            }

            gloveCount = 0;
        }
    }

    private void pickUpItem (Item item)
    {
        ItemType type = item.getType();

        score += type.score;

        if (type == Item.COW_SPAWNER)
        {
            cows.add(Cow.make(item.position.x, item.position.y));
        }

        // destroy nearest platform
        if (type == Item.BOMB_SMALL)
        {
            Iterator<Platform> platformIterator = platforms.iterator();

            float nearestY = 1000;
            Platform nearest = null;

            while (platformIterator.hasNext())
            {
                Platform p = platformIterator.next();

                // don't destroy roof or water
                if (p.isStatic)
                    continue;

                float dist = item.position.y - p.position.y;
                float ix = item.position.x + item.getType().width / 2;

                if (dist >= 0 && dist < nearestY &&
                    ix >= p.position.x && ix < p.position.x + p.width)
                {
                    nearestY = dist;
                    nearest = p;
                }
            }

            if (nearest != null && nearestY < SMALL_BOMB_EXPLOSION_DISTANCE)
            {
                nearest.killMe = true;
            }
        }

        // speed up scroll!
        if (type == Item.STOPWATCH)
        {
            speedUpTimer = random.nextInt(SPEED_UP_MAX_SECONDS) + 1;
        }

        if (type == Item.GLOVE)
        {
            if (item.pickerUpper != null)
            {
                item.pickerUpper.knock(item);
            }
        }

        type.playSound();

        if (type.score > 0)
            app.renderer.popup(item.position.x,
                item.position.y,
                Integer.toString(type.score));
    }

    private void newLevel (float dT)
    {
        worldMovedY += worldScrollSpeed * dT;
        wallsMovedY += worldScrollSpeed * dT;

        if (worldMovedY >= levelHeight)
        {
            worldMovedY = 0;
            makeNewLevel(0, false);
            levelHeight = randomLevelHeight();
        }

        // new walls
        if (wallsMovedY >= Platform.WIDTH)
        {
            boolean doPlace = !randomChance(1, WALL_EMPTY_CHANCE);

            newWall(0, -Platform.WIDTH);
            newWall(Renderer.SCREEN_WIDTH - Platform.THICKNESS,
                    -Platform.WIDTH);

            wallsMovedY = 0;
        }
    }

    private void makeNewLevel (int y, boolean first)
    {
        int count = 0;

        TileType platType = Platform.getRandom();

        for (int i = 0; i < 7; i ++)
        {
            // always leave one open
            if ((randomChance(1, 2) || (i == 6 && count == 6)) &&
                    !(first && i >= 3 && i <=4))
                continue;

            TileType finalType = platType;

            if (randomChance(1, ICE_CHANCE))
                finalType = Platform.ICE;
            else if (randomChance(1, SAND_CHANCE))
                finalType = Platform.QUICKSAND;

            addPlatform(
                    i * 64 + Platform.THICKNESS,
                    y,
                    finalType);
            chanceItem(i, y);
            count ++;
        }

        int tiles = SCREEN_TILES_WIDE * 2 - 1;

        // line of items!
        if (randomChance(1, ITEMS_LINE_CHANCE))
        {
            // 0 == left
            int side = random.nextInt(2);
            // -1, 0, 1, gradient
            float vertical = (float)(random.nextInt(3) - 1) /
                    (float)(random.nextInt(5) + 1);
            int num = random.nextInt(tiles - 3) + 3;
            ItemType type = Item.randomSmall();

            for (int i = 0; i < num; i ++)
            {
                int x;
                float yOffset = 0;

                if (side == 1)
                    x = tiles - i;
                else
                    x = i;

                // bottom to top, so as not to spawn items in the middle of
                // the screen
                if (vertical > 0)
                    yOffset = -num * 32 * vertical;

                addItem(x * 32 + Platform.THICKNESS,
                        (i * vertical) * 32 + yOffset + y,
                        type);
            }
        }
    }

    private void chanceItem (int tileX, int y)
    {
        if (randomChance(1, BIG_ITEM_CHANCE))
        {
            addItem(tileX * 64 + Platform.THICKNESS,
                    Platform.THICKNESS + y,
                    Item.randomBig());
            return;
        }

        if (randomChance(1, COW_SPAWNER_CHANCE))
        {
            addItem(tileX * 64 + Platform.THICKNESS,
                    Platform.THICKNESS + y,
                    Item.COW_SPAWNER);
            return;
        }

        if (randomChance(1, SMALL_BOMB_CHANCE))
        {
            addItem(tileX * 64 + Platform.THICKNESS +
                            Item.BOMB_SMALL.collisionOffsetX,
                    Platform.THICKNESS + y,
                    Item.BOMB_SMALL);
            return;
        }

        if (randomChance(1, STOPWATCH_CHANCE))
        {
            addItem(tileX * 64 + Platform.THICKNESS,
                    Platform.THICKNESS + y,
                    Item.STOPWATCH);
            return;
        }
    }

    private int randomLevelHeight ()
    {
        return LEVEL_HEIGHT_MIN +
                random.nextInt(LEVEL_HEIGHT_MAX - LEVEL_HEIGHT_MIN);
    }

    private void newWall (float x, float y)
    {
        platforms.add(
                Platform.make(x, y, Platform.WALL));
    }

    private void addPlatform (float x, float y, TileType type)
    {
        platforms.add(Platform.make(x, y, type));
    }

    private void addItem (float x, float y, ItemType type)
    {
        items.add(Item.make(x, y, type));
    }

    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public ArrayList<Cow> getCows() {
        return cows;
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
