package sk8_is_lif3.skatetracker;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TrickTrackingTest {
    ArrayList<Trick> trickList = new ArrayList<Trick>();

    @Test
    public void IsAccuratelyTracking(){
        trickList.add(new Trick("Kickflip"));
        Trick trick = trickList.get(0);
        trick.StartTracking();
        double timeEllapsed = trick.EllapsedTime();
        do {
            timeEllapsed = trick.EllapsedTime();
            if(timeEllapsed >= 1.0){
                trick.PauseTracking();
            }
        }while (trick.IsTracking());
        assertEquals(1.0, trick.GetSecondsTracked(), 0.0);
    }
}
