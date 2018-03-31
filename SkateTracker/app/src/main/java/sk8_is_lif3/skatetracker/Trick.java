package sk8_is_lif3.skatetracker;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;
import java.util.Random;
import java.util.logging.LogRecord;

@Entity(tableName = "tricks")
public class Trick extends Trackable {

    @PrimaryKey
    @NonNull
    public String _static_id;

    @ColumnInfo(name = "name")
    public String _name;

    @ColumnInfo(name = "timesLanded")
    public int _timesLanded;

    public Trick(String name) {
        super();
        _name = name;
        _static_id = GenerateID();
    }

    public String GetName() {
        return _name;
    }
    public String GetID() {
        return _static_id;
    }
    public int GetTimesLanded(){ return _timesLanded; }

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
