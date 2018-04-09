package sk8_is_lif3.skatetracker.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import sk8_is_lif3.skatetracker.Session;
import sk8_is_lif3.skatetracker.Trick;

@Dao
public interface SessionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertSession(Session session);

    @Update()
    public void updateSession(Session session);

    @Delete()
    public void deleteSession(Session session);

    @Query("SELECT * from sessions")
    public List<Session> getSessions();

    @Query("SELECT * from sessions WHERE _static_id = :sessionID")
    public Session getSession(String sessionID);

}
