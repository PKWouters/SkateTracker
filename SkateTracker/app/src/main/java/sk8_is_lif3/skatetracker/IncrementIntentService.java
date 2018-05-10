package sk8_is_lif3.skatetracker;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class IncrementIntentService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public IncrementIntentService(){
        super("IncrementIntent");
    }

    public IncrementIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager lBM = LocalBroadcastManager.getInstance(this);
        intent.setAction("trick_landed");
        lBM.sendBroadcast(intent);
    }
}
