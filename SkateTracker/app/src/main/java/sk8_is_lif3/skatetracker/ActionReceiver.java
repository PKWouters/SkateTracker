package sk8_is_lif3.skatetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context,final Intent intent){
        Intent intent2 = new Intent();
        intent2.setAction("trick_landed");

        // Rebroadcasts to your own receiver.
        // This receiver is not exported; it'll only be received if the receiver is currently registered.
        context.sendBroadcast(intent2);
    }
}
