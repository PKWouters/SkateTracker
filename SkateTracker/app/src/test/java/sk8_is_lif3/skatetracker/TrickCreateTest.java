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
    ArrayList<Trick> trickList = new ArrayList<Trick>();

    @Test
    public void DidCreateTrick() {
        trickList.add(new Trick("Kickflip"));
        assertEquals(1, trickList.size());
    }

    @Test
    public void DidStartTracking(){
        trickList.add(new Trick("Kickflip"));
        Trick trick = trickList.get(0);
        trick.StartTracking();
        assertEquals(true, trick.IsTracking());
    }
    @Test
    public void DidEndTracking(){
        trickList.add(new Trick("Kickflip"));
        Trick trick = trickList.get(0);
        trick.StartTracking();
        trick.PauseTracking();
        assertEquals(false, trick.IsTracking());
    }
}