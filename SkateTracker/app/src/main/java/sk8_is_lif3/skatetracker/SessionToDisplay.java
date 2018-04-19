package sk8_is_lif3.skatetracker;

import java.util.ArrayList;
import java.util.Map;

public class SessionToDisplay {
    public String _date, _id, _totalTime, _uID;
    public ArrayList<Map<String, Object>> _tricks;

    public SessionToDisplay(String date, String id, String totalTimeFormatted, String user_id, ArrayList<Map<String, Object>> tricks){
        _date = date;
        _id = id;
        _totalTime = totalTimeFormatted;
        _uID = user_id;
        _tricks = tricks;
    }

    public String GetDate(){ return _date; }
    public String GetID(){ return _id; }
    public String GetTotalTime(){ return _totalTime; }
    public String GetUserID(){ return _uID; }
    public ArrayList<Map<String, Object>> GetTricks(){ return _tricks; }


}
