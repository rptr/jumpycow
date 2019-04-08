package com.rptr.jumpy2;

import com.badlogic.gdx.Gdx;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by rpj on 7/25/17.
 */
public class Cow extends Entity {
    // amount at which velocity clamps to 0, tilt ranges from 0 to 9.8
    // i.e. nothing happens below this tilt level
    public static float TILT_X_CLAMP = 1.0f;
    public static float VELOCITY_X_CLAMP = 20.0f;
    public static float COW_ACCELERATION = 100.0f;
    public static boolean MOVE_IN_AIR = true;
    public static float JUMP_VELOCITY = 250;
    public static float DROWN_SPEED = 40.0f;
    public static float QUICKSAND_SPEED_MOD = 0.25f;
    public static float ICE_SPEED_MOD = 4.0f;
    static float KNOCK_VELOCITY_X = 800;
    static float KNOCK_VELOCITY_Y = 300;

    // platform this cow is standing on
    private Platform myPlatform = null;
    private float animState = 0;
    public boolean isDrowning = false;

    public int direction = 1;

    static Cow make (float x, float y)
    {
        Cow c = new Cow ();
        c.position.set(x, y);
        Jukebox.playMoo();
        return c;
    }

    public Cow ()
    {
        super();
        maxSpeedX = 400.0f;
        width = 55;
        collisionOffsetX = 5;
        defaultHeight = 64;
        height = defaultHeight;
    }

    public float getAnimState (float dT)
    {
        float mod = 1;

        if (velocity.y == 0 && velocity.x != 0)
        {
            mod = Math.abs(velocity.x / maxSpeedX);
        }

        animState += dT * mod;
        return animState;
    }

    public void run (float dT,
                     ArrayList<Platform> platforms,
                     boolean shake,
                     ArrayList<Item> items,
                     ArrayList<Cow> cows,
                     Game game)
    {
        float waterLevel = game.getWaterLevel(game.score);

        if (position.y < waterLevel - height ||
                position.y > Renderer.SCREEN_HEIGHT)
        {
            killMe = true;
            return;
        }

        // drown
        if (isDrowning)
        {
            position.y -= DROWN_SPEED * dT;
            return;
        }

        if (position.y <= waterLevel)
        {
            isDrowning = true;
            Jukebox.cowDeath.play();
            return;
        }

        // decelerate
        if (Math.abs(tilt) < TILT_X_CLAMP)
        {
            if (velocity.x > 0)
                velocity.x =
                        Math.max(velocity.x - dT * COW_ACCELERATION, 0);

            if (velocity.x < 0)
                velocity.x =
                        Math.min(velocity.x + dT * COW_ACCELERATION, 0);

            if (Math.abs(velocity.x) < VELOCITY_X_CLAMP)
                velocity.x = 0;

        } else
        {
            float mod = 1;

            if (myPlatform != null && myPlatform.type == Platform.ICE)
                mod = ICE_SPEED_MOD;
            if (myPlatform != null && myPlatform.type == Platform.QUICKSAND)
                mod = QUICKSAND_SPEED_MOD;

            velocity.x += tilt * COW_ACCELERATION * dT * mod;

            if (velocity.x > 0)
            {
                direction = 1;
            } else if (velocity.x < 0)
            {
                direction = -1;
            }
        }

        if (velocity.x > maxSpeedX)
            velocity.x = maxSpeedX;
        else if (velocity.x < -maxSpeedX)
            velocity.x = -maxSpeedX;

        // JUMP
        if (velocity.y == 0 && shake)
        {
            Jukebox.playJump();
            velocity.y = JUMP_VELOCITY;
            myPlatform = null;

        } else {
            velocity.y -= Game.GRAVITY * dT;
        }

        if (velocity.y <= -Game.TERMINAL_VELOCITY)
        {
            velocity.y = -Game.TERMINAL_VELOCITY;
        }

        // wrap around
        if (position.x < -width)
        {
            position.x += Renderer.SCREEN_WIDTH;

        } else if (position.x >= Renderer.SCREEN_WIDTH)
        {
            position.x -= Renderer.SCREEN_WIDTH;
        }

        float posy = position.y + velocity.y * dT;
        float posx = position.x + velocity.x * dT;

        // make sure cow remains on top of its platform
        if (myPlatform != null)
        {
            posy = myPlatform.position.y + myPlatform.height;
            velocity.y = 0;

            height = Math.min(Renderer.SCREEN_HEIGHT -
                    Platform.THICKNESS -
                    (int)position.y,
                    defaultHeight);

            if (height <= 1)
            {
                killMe = true;
                Jukebox.cowDeath.play();
                return;
            }
        } else
        {
            height = defaultHeight;
        }

        myPlatform = null;

        Iterator<Cow> cowIterator = cows.iterator();

        // collide with other cows
        while (cowIterator.hasNext())
        {
            Cow cow = cowIterator.next();

            if (cow == this)
                continue;

            if (isTouching(cow))
            {
                if (posx > cow.position.x)
                    velocity.x += COW_ACCELERATION * dT;
                else if (posx < cow.position.x)
                    velocity.x -= COW_ACCELERATION * dT;
//                else
//                {
//                    posx += 1;
//                    cow.position.x -= 1;
//                    // XXX TODO don't go out of walls
//                }
            }
        }

        // platform collision
        for (Platform p : platforms)
        {
            // NOTE water is solid
            if (!p.isSolid || p.isWater)
            {
                continue;
            }

            // don't bother if it's too far away
            if (posx + width * 2 < p.position.x ||
                    posx > p.position.x + p.width * 2 ||
                    posy > p.position.y + p.height * 2 ||
                    posy + height * 2 < p.position.y)
                continue;

            // y collision
            if (position.x + collisionOffsetX < p.position.x + p.width &&
                    position.x - collisionOffsetX + width > p.position.x)
            {

                // floor collision
                if (((position.y <= p.position.y + p.height &&
                        position.y >= p.position.y) ||
                        (posy <= p.position.y + p.height &&
                                posy >= p.position.y)) &&
                        velocity.y <= 0)
                {
                    posy = p.position.y + p.height;
                    myPlatform = p;

                    // initial collision
                    if (velocity.y < 0)
                    {
                        Jukebox.thud.play();

                        if (p.type == Platform.ICE)
                        {
                            velocity.x *= 2;
                        }

                        if (p.type == Platform.QUICKSAND)
                        {
                            velocity.x /= 2;
                        }
                    }

                    velocity.y = 0;
                }

                // ceiling collision

                if (posy + height >= p.position.y &&
                        posy + height <= p.position.y + p.height)
                {
                    posy = p.position.y - height;
                    velocity.y = 0;
                }
            }

            if (posy < p.position.y + p.height &&
                    posy + height> p.position.y &&
                    velocity.x != 0) {

                // right side
                if (posx + collisionOffsetX + width > p.position.x &&
                        posx + collisionOffsetX + width < p.position.x + p.width)
                {
                    posx = p.position.x - width - collisionOffsetX;
                    velocity.x = 0;
                }

                // left
                if (posx + collisionOffsetX < p.position.x + p.width &&
                        posx + collisionOffsetX > p.position.x)
                {
                    posx = p.position.x + p.width - collisionOffsetX;
                    velocity.x = 0;
                }
            }
        }

        Iterator<Item> itemIterator = items.iterator();

        while (itemIterator.hasNext())
        {
            Item i = itemIterator.next();

            if (isTouching(i))
            {
                i.pickUp(this);
            }
        }

        position.set(posx, posy);
    }

    public void knock (Item i)
    {
        // knock from left
        if (i.flyDir == 1)
        {
            velocity.x += KNOCK_VELOCITY_X;
            velocity.y += KNOCK_VELOCITY_Y;

        } else
        {
            velocity.x -= KNOCK_VELOCITY_X;
            velocity.y += KNOCK_VELOCITY_Y;
        }
    }

    public boolean isTouching (BaseEntity other)
    {
        return (position.x + collisionOffsetX + width >=
                other.position.x + other.getCollisionOffsetX() &&
                position.x + collisionOffsetX <=
                other.position.x + other.getWidth() + other.getCollisionOffsetX() &&
                position.y <= other.position.y + other.getHeight() + other.getCollisionOffsetY() &&
                position.y + height >= other.position.y + other.getCollisionOffsetY());
    }
}
