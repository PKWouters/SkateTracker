package sk8_is_lif3.skatetracker;

import java.util.ArrayList;
import java.util.Map;

public class SessionToDisplay {
    public String mDate, mId, mTotalTime, mUid;
    public ArrayList<Map<String, Object>> mTricks;

    public SessionToDisplay(){

    }

    public SessionToDisplay(String date, String id, String totalTimeFormatted, String user_id, ArrayList<Map<String, Object>> tricks){
        mDate = date;
        mId = id;
        mTotalTime = totalTimeFormatted;
        mUid = user_id;
        mTricks = tricks;
    }

    public String getDate(){ return mDate; }
    public String getId(){ return mId; }
    public String getTotalTimeFormatted(){ return mTotalTime; }
    public String getUid(){ return mUid; }
    public ArrayList<Map<String, Object>> getTricks(){ return mTricks; }

    public void setDate(String date){ mDate = date; }
    public void setId(String id){ mId = id; }
    public void setTotalTimeFormatted(String time){ mTotalTime = time; }
    public void setUID(String user_id){ mUid = user_id; }
    public void setTricks(ArrayList<Map<String, Object>> tricks){ mTricks = tricks; }



}
