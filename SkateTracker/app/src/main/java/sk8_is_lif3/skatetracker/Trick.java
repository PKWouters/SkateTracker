package sk8_is_lif3.skatetracker;

import android.os.Handler;
import java.util.logging.LogRecord;

public class Trick extends Trackable{

    private String _name;
    private int _timesLanded;

    public Trick(String name) {
        super();
        _name = name;
    }
    public int GetTimesLanded(){ return _timesLanded; }
    public String GetName(){ return _name; }


}
