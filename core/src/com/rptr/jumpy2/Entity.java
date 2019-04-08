package com.rptr.jumpy2;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

public class Entity extends BaseEntity {
    public static int WIDTH = 64;

    public static Entity make (Entity ent, float x, float y)
    {
        ent.position.set(x, y);
        return ent;
    }

	final Vector2 velocity = new Vector2();
	float maxSpeedX = 1.0f;
	float tilt = 0;

	public Entity ()
	{
	    super();
	    velocity.set(0, 0);
	}

	public void run (float dT)
    {
    }
}
