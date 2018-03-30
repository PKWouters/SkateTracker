package sk8_is_lif3.skatetracker;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

@Entity
public class Trackable {

    @Ignore
    private boolean _isTracking = false;
    @Ignore
    private int _trackerStarted, _trackerEnded;
    @Ignore
    private int _trackedHours, _trackedMinutes, _trackedSeconds;

    public Trackable(){
        _trackerStarted = _trackerEnded = 0;
        _trackedSeconds = _trackedHours = _trackedMinutes = 0;
    }

    public double GetSecondsTracked(){ return _trackedSeconds; }
    public double GetMinutesTracked(){ return _trackedMinutes; }
    public double GetHoursTracked(){ return _trackedHours; }
    public boolean IsTracking(){ return _isTracking; }

    public void StartTracking(){
        if(!_isTracking) {
            _isTracking = true;
            if(_trackerStarted == 0)
                _trackerStarted = (int) (System.currentTimeMillis());
            else
                _trackerStarted += (int)(System.currentTimeMillis()) - _trackerEnded;
        }
    }

    public void PauseTracking(){
        if(_isTracking) {
            _isTracking = false;
            _trackerEnded = (int) (System.currentTimeMillis());
            _trackedSeconds += (int)((_trackerEnded - _trackerStarted) / 1000.0);
            //Convert Values Over Accordingly
            _trackedMinutes = _trackedSeconds / 60;
            _trackedHours = _trackedMinutes / 60;
            _trackedMinutes = _trackedMinutes % 60;
            _trackedSeconds = _trackedSeconds % 60;
        }
    }

    public void ResetTracking(){
        _isTracking = false;
        _trackerStarted = 0;
        _trackerEnded = 0;
        _trackedSeconds = 0;
        _trackedMinutes = 0;
        _trackedHours = 0;
    }

    public String EllapsedTime(){
        if(_isTracking) {
            int now = (int)(System.currentTimeMillis());
            _trackedSeconds = (int)((now - _trackerStarted) / 1000.0);
            _trackedMinutes = _trackedSeconds / 60;
            _trackedHours = _trackedMinutes / 60;
            _trackedMinutes = _trackedMinutes % 60;
            _trackedSeconds = _trackedSeconds % 60;
            //return String.format("%d", _trackerStarted);
        }else{
            _trackedSeconds = (int)((_trackerEnded - _trackerStarted) / 1000.0);
            _trackedMinutes = _trackedSeconds / 60;
            _trackedHours = _trackedMinutes / 60;
            _trackedMinutes = _trackedMinutes % 60;
            _trackedSeconds = _trackedSeconds % 60;
        }
        return String.format("%d::%d::%d", _trackedHours, _trackedMinutes, _trackedSeconds);
    }
}
