package com.rptr.jumpy2;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by rpj on 7/27/17.
 */
public class Popup {

    public float x, y;
    public String text;
    public long birthTime;
    public Vector2 velocity;

    public Popup (float x, float y, String text, long time)
    {
        this.x = x;
        this.y = y;
        this.text = text;
        birthTime = time;
        velocity = new Vector2(Game.random.nextFloat() % 100 - 50,
                Game.random.nextFloat() % 100 - 50);
    }

    public void run (float dT)
    {
        x += velocity.x * dT;
        y += velocity.y * dT;
    }
}
