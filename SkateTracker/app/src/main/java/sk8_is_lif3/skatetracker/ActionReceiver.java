package sk8_is_lif3.skatetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context,final Intent intent){
        Intent intent2 = new Intent();
        intent2.setAction("Add Trick");

        context.sendBroadcast(intent2);
    }
}
