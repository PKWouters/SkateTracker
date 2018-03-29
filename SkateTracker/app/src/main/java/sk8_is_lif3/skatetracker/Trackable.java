package sk8_is_lif3.skatetracker;

public class Trackable {

    private boolean _isTracking = false;

    private int _trackerStarted = 0, _trackerEnded = 0;

    private int _trackedHours = 0, _trackedMinutes = 0, _trackedSeconds = 0;

    public Trackable(){

    }

    public double GetSecondsTracked(){ return _trackedSeconds; }
    public double GetMinutesTracked(){ return _trackedMinutes; }
    public double GetHoursTracked(){ return _trackedHours; }
    public boolean IsTracking(){ return _isTracking; }

    public void StartTracking(){
        _trackerStarted = (int)(System.currentTimeMillis());
        if(!_isTracking)
            _isTracking = true;
    }

    public void PauseTracking(){
        if(_isTracking)
            _isTracking = false;
        _trackerEnded = (int)(System.currentTimeMillis());
        _trackedSeconds = (_trackerEnded - _trackerStarted) / 1000;

        //Convert Values Over Accordingly
        _trackedMinutes = _trackedSeconds / 60;
        _trackedHours = _trackedMinutes / 60;
        _trackedMinutes = _trackedMinutes % 60;
        _trackedSeconds = _trackedSeconds % 60;
    }

    public void ResetTracking(){
        _isTracking = false;
        _trackerStarted = 0;
        _trackerEnded = 0;
        _trackedSeconds = 0;
        _trackedMinutes = 0;
        _trackedHours = 0;
    }

    public int EllapsedTime(){
        if(_isTracking) {
            int now = (int) (System.currentTimeMillis());
            return (now - _trackerStarted) / 1000;
        }
        return _trackerStarted;
    }
}
