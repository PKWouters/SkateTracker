package sk8_is_lif3.skatetracker;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TrickCreateTest {
    @Test
    public void Didaddtrick(){
        Session whatever = new Session();
        whatever.addtrick(new Trick("kickflip"));
        assertEquals(1,whatever.getTricksadded());
    }
    @Test
    public void IsAccuratelyTracking(){
        Session whatever = new Session();
        whatever.addtrick(new Trick("Kickflip"));
        Trick trick = whatever.getTricksadded().get(0);
        whatever.StartTracking();
        trick.StartTracking();
        double timeEllapsed = whatever.EllapsedTime();
        double trickEllapsedTime = trick.EllapsedTime();
        do {
            timeEllapsed = whatever.EllapsedTime();
            trickEllapsedTime = trick.EllapsedTime();
            if(trickEllapsedTime >= 1.0){
                trick.PauseTracking();
            }
            if(timeEllapsed >= 2.0){
                whatever.PauseTracking();
            }
        }while (whatever.IsTracking());
        assertEquals(1.0,trick.GetSecondsTracked(), 0.0);
        //assertEquals(2.0,whatever.GetSecondsTracked(), 0.0);

    }
}