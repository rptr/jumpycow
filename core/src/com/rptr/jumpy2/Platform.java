package com.rptr.jumpy2;

/**
 * Created by rpj on 7/25/17.
 */

public class Platform extends BaseEntity {

    // these are set up in JumpyGame.java
    public static TileType GRASS;
    public static TileType ICE;
    public static TileType WATER;
    public static TileType ROOF;
    public static TileType WALL;
    public static TileType QUICKSAND;

    public static int THICKNESS = 16;
    static int WIDTH = 64;

    public static TileType getRandom ()
    {
        return GRASS;
    }

    // NOTE quite pointless and wasteful to keep a reference in every
    //      platform
    public TileType type;

    static Platform make (float x, float y, TileType type)
    {
        Platform p = new Platform (type);
        p.position.set(x, y);
        return p;
    }

    Platform (TileType type)
    {
        this.type = type;
        // XXX should just remove width/height from BaseEntity but meh
        width = type.width;
        height = type.height;
        isSolid = type.isSolid;
        isStatic = type.isStatic;
        isWater = (type == Platform.WATER);
    }
}
