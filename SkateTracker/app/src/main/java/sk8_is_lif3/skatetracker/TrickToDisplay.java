package sk8_is_lif3.skatetracker;

import java.util.ArrayList;
import java.util.Map;

public class TrickToDisplay {
    public String mName, mDbID;
    double mAvgRatio;
    int mTotalLandings;
    ArrayList<Map<String, Object>> mSessions;

    public TrickToDisplay(){

    }

    public TrickToDisplay(String name, double ratio, int totalLandings, String dbID, ArrayList<Map<String, Object>> sessions){
        mName = name;
        mAvgRatio = ratio;
        mTotalLandings = totalLandings;
        mSessions = sessions;
        mDbID = dbID;

    }

    public String getName(){ return mName; }
    public String getDbID(){ return mDbID; }
    public double getAvgRatio(){ return mAvgRatio; }
    public int getTotalLandings(){ return mTotalLandings; }
    public ArrayList<Map<String, Object>> getSessions() { return mSessions; }

    public void setName(String name){ mName = name; }
    public void setDbID(String dbID){ mDbID = dbID; }
    public void setAvgRatio(double ratio){ mAvgRatio = ratio; }
    public void setTotalLandings(int totalLandings){ mTotalLandings = totalLandings; }
    public void setSessions(ArrayList<Map<String, Object>> sessions) { mSessions = sessions; }


}
