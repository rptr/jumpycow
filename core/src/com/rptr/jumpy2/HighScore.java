package com.rptr.jumpy2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by rpj on 7/27/17.
 */
public class HighScore {

    public int myRecord = 0;
    public boolean madeNewRecord = false;

    public HighScore ()
    {
        FileHandle f = Gdx.files.local("score");

        // first play
        if (!f.exists())
        {

        } else {
            String t = f.readString();
            Gdx.app.log("poop", "score "+t);
            myRecord = 0;
        }
    }

    public void gameOver (int score)
    {
        madeNewRecord = false;

        if (score > myRecord)
        {
            madeNewRecord = true;
            setNewRecord(score);
        }
    }

    public void setNewRecord (int score)
    {
        myRecord = score;

        FileHandle f = Gdx.files.local("score");
        f.writeString(Integer.toString(score), false);
    }
}
