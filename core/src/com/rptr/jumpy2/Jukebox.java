package com.rptr.jumpy2;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by rpj on 7/27/17.
 */
public class Jukebox {

    static float MUSIC_VOLUME = 0.8f;

    static public Sound moo1;
    static public Sound moo2;
    static public Sound moo3;
    static public Sound moo4;
    static public Sound moo5;

    static public Sound cowDeath;

    static public Sound pling1;
    static public Sound pling2;
    static public Sound pling3;

    static public Sound thud;
    static public Sound explosion;

    static public Sound jump1;
    static public Sound jump2;
    static public Sound jump3;

    static public Sound splash;
    static public Sound pour;

    static Sound bif1;
    static Sound bif2;

    static Sound clock;
    static Sound rocket;

    static Music music;

    static private float musicVolume = 0;
    static private float fadeMult = 0;

    static public void playMoo ()
    {
        Sound[] moos = {moo1, moo2, moo3, moo4, moo5};
        moos[Game.random.nextInt(moos.length)].play();
    }

    static public void playPling ()
    {
        Sound[] plings = {pling1, pling2, pling3};
        plings[Game.random.nextInt(plings.length)].play();
    }

    static public void playJump ()
    {
        Sound[] jumps = {jump1, jump2, jump3};
        jumps[Game.random.nextInt(jumps.length)].play();
    }

    static public void playExplosion ()
    {
        long id = Jukebox.explosion.play();
        Jukebox.explosion.setVolume(id, 1.5f);
    }

    static void playBif ()
    {
        Sound[] bifs = {bif1, bif2};
        bifs[Game.random.nextInt(bifs.length)].play();
    }

    static public void playClock ()
    {
        long id = Jukebox.clock.play();
        Jukebox.clock.setVolume(id, 1.5f);
    }

    static public void startMusic ()
    {
        musicVolume = 0;
        fadeMult = 1;

        music.play();
        music.setVolume(MUSIC_VOLUME);
        music.setLooping(true);
    }

    static public void stopMusic ()
    {
        fadeMult = -1;
    }

    static public void run (float dT)
    {
        if (fadeMult != 0)
        {
            musicVolume += dT * fadeMult;

            if (musicVolume <= 0)
            {
                musicVolume = 0;
                fadeMult = 0;
                music.stop();
            }

            if (musicVolume >= MUSIC_VOLUME)
            {
                musicVolume = MUSIC_VOLUME;
                fadeMult = 0;
            }

            music.setVolume(musicVolume);
        }
    }
}
