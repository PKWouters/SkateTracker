package sk8_is_lif3.skatetracker;

import android.arch.persistence.room.Entity;

import java.util.ArrayList;


public class Session extends Trackable
{
    private float starttime;
    private float endtime;
    private float tst;
    //tst = total session time

    public Session(){}

    ArrayList<Trick> tricksadded = new ArrayList<Trick>();

    public float getStarttime()
    {
        return starttime;
    }

    public float getEndtime()
    {
        return endtime;
    }

    public ArrayList<Trick> getTricksadded()
    {
        return tricksadded;
    }

    public void setStarttime(float newstarttime)
    {
        starttime = newstarttime;
    }

    public void setEndtime(float newendtime)
    {
        endtime = newendtime;
    }

    public void addtrick(Trick trick)
    {
            tricksadded.add(trick);
    }

    public Trick searchtrick(String name)
    {
        for (Trick trick:tricksadded)
        {
            if (trick.GetName() == name)
            {
                return trick;
            }
        }
        return null;
    }
}
