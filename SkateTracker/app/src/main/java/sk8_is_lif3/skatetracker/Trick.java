package sk8_is_lif3.skatetracker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;
import java.util.Random;
import java.util.logging.LogRecord;

public class Trick extends Trackable {
    public String _static_id;

    public String _database_id;

    public String _name;

    public int _timesLanded;

    public Trick(String name, String dbID) {
        super();
        _name = name;
        _static_id = GenerateID();
        _database_id = dbID;
    }

    public String GetName() {
        return _name;
    }
    public String GetID() {
        return _static_id;
    }
    public String GetDBID(){ return _database_id; }
    public int GetTimesLanded(){ return _timesLanded; }
    public double GetRatio(){
        return (GetTimesLanded() / (GetTotalSecondsTracked()/60));
    }

    public void IncrementTimesLanded(){
        if(IsTracking())
            _timesLanded++;
    }

    private String GenerateID() {
        //CREATE STATIC ID
        String ret = "";
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lower = upper.toLowerCase(Locale.ROOT);
        final String digits = "0123456789";
        final String alphanum = upper + lower + digits;
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int randIndex = Math.abs(random.nextInt()) % alphanum.length();
            char lett = alphanum.charAt(randIndex);
            ret += Character.toString(lett);
        }
        return ret;
    }
}
