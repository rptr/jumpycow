package com.rptr.jumpy2;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BaseEntity {
	public final Vector2 position = new Vector2();
//	public final Rectangle bounds = new Rectangle();
    int width = 1;
    int height = 1;
    // isWater is a desperate afterthought tacked on innit
    boolean killMe = false, isSolid, isStatic, isWater = false;
    int collisionOffsetX = 0;
    int collisionOffsetY = 0;
    public float speedFactor = 1;
    int defaultHeight = 1;

	public BaseEntity ()
	{
        position.set(0, 0);
//        bounds.set(0, 0, 1, 1);
	}

    public void scroll (float dT, float scrollSpeed, int score)
    {
        if (isWater)
        {
            float extraY = Game.getWaterLevel(score);
            position.y = extraY;
        }

        if (isStatic)
            return;

        position.y += dT * scrollSpeed * speedFactor;

        // kill them when they go above the screen
        if (position.y > Renderer.SCREEN_HEIGHT)
        {
            killMe = true;
        }
    }

    public int getWidth ()
    {
        return width;
    }

    public int getHeight ()
    {
        return height;
    }

    public int getCollisionOffsetX ()
    {
        return collisionOffsetX;
    }

    public int getCollisionOffsetY ()
    {
        return collisionOffsetY;
    }
}
