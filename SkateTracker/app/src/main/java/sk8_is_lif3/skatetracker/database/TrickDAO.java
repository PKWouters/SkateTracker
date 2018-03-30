package sk8_is_lif3.skatetracker.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import sk8_is_lif3.skatetracker.Trick;

@Dao
public interface TrickDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTrick(Trick trick);

    @Update()
    public void updateTrick(Trick trick);

    @Delete()
    public void deleteTrick(Trick trick);

    @Query("SELECT * from tricks")
    public List<Trick> getTricks();

    @Query("SELECT * from tricks WHERE _static_id = :trickID")
    public Trick getTricks(String trickID);

}
