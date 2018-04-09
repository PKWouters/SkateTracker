package sk8_is_lif3.skatetracker;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Entity(tableName = "sessions")
public class Session extends Trackable
{


    @PrimaryKey
    @NonNull
    public String _static_id;

    @ColumnInfo(name = "startTime")
    public int starttime;

    @ColumnInfo(name = "endTime")
    public int endtime;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "tricksAdded")
    public String tricksAddedString;

    @Ignore
    ArrayList<Trick> tricksadded = new ArrayList<Trick>();

    public Session(){
        date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        _static_id = GenerateID();
        tricksAddedString = ConvertToJSON(tricksadded);
    }

    public int GetStartTime()
    {
        return starttime;
    }

    public int GetEndTime()
    {
        return endtime;
    }

    public String GetDate(){ return date; }

    public String GetID(){ return _static_id; }

    public ArrayList<Trick> GetTricksAdded()
    {
        return ConvertJSONtoList(tricksAddedString);
    }

    public void SetStartTime(int newstarttime)
    {
        starttime = newstarttime;
    }

    public void SetEndTime(int newendtime)
    {
        endtime = newendtime;
    }



    public void AddTrick(Trick trick)
    {
            tricksadded.add(trick);
            tricksAddedString = ConvertToJSON(tricksadded);
    }

    public Trick SearchTrick(String name)
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

    private String GenerateID() {
        //CREATE STATIC ID
        String ret = "";
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lower = upper.toLowerCase(Locale.ROOT);
        final String digits = "0123456789";
        final String alphanum = upper + lower + digits;
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int randIndex = Math.abs(random.nextInt()) % alphanum.length();
            char lett = alphanum.charAt(randIndex);
            ret += Character.toString(lett);
        }
        return ret;
    }

    @TypeConverter
    public String ConvertToJSON(List<?> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    public ArrayList<Trick> ConvertJSONtoList(String value) {
        Type listType = new TypeToken<List<Trick>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
}
