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

        // Rebroadcasts to your own receiver.
        // This receiver is not exported; it'll only be received if the receiver is currently registered.
        context.sendBroadcast(intent2);

        //Toast.makeText(context, "CLICKED!", Toast.LENGTH_SHORT).show();
        //if (intent.getAction().equalsIgnoreCase("Trick Landed")){
            //currentTrick.IncrementTimesLanded();
        //}
    }
    //String action = intent.getStringExtra("Trick Landed ");
    //if (action.equals("Trick Landed ")) {
    //preformAction1();
    //}
    //Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    //context.sendBroadcast(it);
    //}
    //public void preformAction1(){
    //currentTrick.PauseTracking();
    //}
}
