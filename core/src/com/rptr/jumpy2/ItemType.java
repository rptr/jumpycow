package com.rptr.jumpy2;

import java.util.ArrayList;

/**
 * Created by rpj on 7/25/17.
 */

public class ItemType
{
    static public int COUNT = 0;

    static public ArrayList<ItemType> allTypes = new ArrayList<ItemType>();

    public int id;
    public int width, height;
    public boolean isStatic;
    public int score = 0;
    public int collisionOffsetX;
    public int collisionOffsetY;
    public float speedFactor = 1;
    public boolean doBob = true;

    public ItemType (int w, int h, boolean isStatic, int score,
                     int offsetX, int offsetY, float speedFactor)
    {
        id = COUNT;
        allTypes.add(this);
        COUNT ++;

        width = w;
        height = h;
        this.isStatic = isStatic;
        this.score = score;
        collisionOffsetX = offsetX;
        collisionOffsetY = offsetY;
        this.speedFactor = speedFactor;
    }

    public void playSound ()
    {
        if (this == Item.BOTTLE_BIG || this == Item.MILK_CAN)
            Jukebox.splash.play();

        else if (Item.CARTON_BIG == this ||
                Item.CARTON_BIG_GREEN == this ||
                Item.CARTON_BIG_RED == this)
        {
            Jukebox.pour.play();

        } else if (Item.STOPWATCH == this)
        {
            Jukebox.playClock();

        } else if (Item.BOMB_SMALL == this)
        {
            Jukebox.playExplosion();

        } else if (Item.GLOVE == this)
        {
            Jukebox.playBif();

        }else if (width <= 32)
        {
            Jukebox.playPling();
        }
    }
}