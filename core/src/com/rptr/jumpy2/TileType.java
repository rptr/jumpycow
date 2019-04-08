package com.rptr.jumpy2;

/**
 * Created by rpj on 7/25/17.
 */

public class TileType
{
    static public int COUNT = 0;

    public int id;
    public int width, height;
    public boolean isSolid, isStatic, isDeadly;

    public TileType ()
    {
    }

    public TileType (int w, int h, boolean solid, boolean isStatic)
    {
        id = COUNT;
        COUNT ++;
        width = w;
        height = h;
        isSolid = solid;
        this.isStatic = isStatic;
    }
}