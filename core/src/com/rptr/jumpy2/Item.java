package com.rptr.jumpy2;

/**
 * Created by rpj on 7/25/17.
 */
public class Item extends BaseEntity {

    static public ItemType BOTTLE_BIG;
    static public ItemType BOTTLE_SMALL;

    static public ItemType MILK_CAN;

    static public ItemType CARTON_BIG;
    static public ItemType CARTON_BIG_GREEN;
    static public ItemType CARTON_BIG_RED;

    static public ItemType CARTON_SMALL;
    static public ItemType CARTON_SMALL_GREEN;
    static public ItemType CARTON_SMALL_RED;

    static public ItemType COW_SPAWNER;

    static public ItemType BOMB_SMALL;
    static public ItemType STOPWATCH;

    static public ItemType GLOVE;

    static public float MAX_BOB = 12.0f;
    static public float ANIM_SPEED = 40.0f;
    static float GLOVE_SPEED = 400;

    // "floating"/ bobbing effect
    public float floatAnim = 0;
    public int floatDir = 1;
    public int flyDir = 0;
    public boolean pickMeUp = false;
    public Cow pickerUpper = null;

    static Item make (float x, float y, ItemType type)
    {
        Item i = new Item(type);
        i.position.set(x, y);

        if (x < 0)
            i.flyDir = 1;
        else
            i.flyDir = -1;

        i.isStatic = !type.isStatic;

        return i;
    }

    static ItemType type (int type)
    {
        return ItemType.allTypes.get(type);
    }

    static ItemType randomBig ()
    {
        ItemType[] bigItems = {BOTTLE_BIG, MILK_CAN, CARTON_BIG,
            CARTON_BIG_GREEN, CARTON_BIG_RED};
        return bigItems[Game.random.nextInt(bigItems.length)];
    }

    static ItemType randomSmall ()
    {
        ItemType[] smallItems = {BOTTLE_SMALL, CARTON_SMALL,
            CARTON_SMALL_GREEN, CARTON_SMALL_RED};
        return smallItems[Game.random.nextInt(smallItems.length)];
    }

    public int type;

    Item (ItemType type)
    {
        super();
        this.type = type.id;
        floatAnim = Game.random.nextFloat() % MAX_BOB;
        speedFactor = type.speedFactor;
    }

    public ItemType getType ()
    {
        return ItemType.allTypes.get(type);
    }

    public void bob (float dT)
    {
        if (!getType().doBob)
            return;

        floatAnim += dT * floatDir * ANIM_SPEED;

        if (floatAnim >= MAX_BOB)
        {
            floatDir = -1;
            floatAnim = MAX_BOB;

        } else if (floatAnim <= 0)
        {
            floatDir = 1;
            floatAnim = 0;
        }
    }

    public void pickUp (Cow cow)
    {
        killMe = true;
        pickMeUp = true;
        pickerUpper = cow;
    }

    public void run (float dT)
    {
        if (getType().isStatic)
            return;

        position.x += flyDir * dT * GLOVE_SPEED;

        if (position.x < -32 || position.x >= Renderer.SCREEN_WIDTH)
        {
            killMe = true;
        }
    }

    public int getCollisionOffsetX ()
    {
        return Item.type(type).collisionOffsetX;
    }

    public int getCollisionOffsetY ()
    {
        return Item.type(type).collisionOffsetY;
    }

    public int getWidth ()
    {
        return Item.type(type).width;
    }

    public int getHeight ()
    {
        return Item.type(type).height;
    }
}
