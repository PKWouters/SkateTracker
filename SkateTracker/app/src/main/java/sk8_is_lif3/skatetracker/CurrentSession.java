package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map;

import sk8_is_lif3.skatetracker.database.AppDatabase;

public class CurrentSession extends AppCompatActivity /*implements SensorEventListener*/ {


    private RecyclerView trickRecyclerView;
    private RecyclerView.Adapter trickAdapter;
    private RecyclerView.LayoutManager trickLayoutManager;
    private AppDatabase database;
    List<Trick> tempTrickList;
    ArrayList<String> sessionIDs;
    Session currentSession;
    Trick currentTrick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_session);

        /*
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        */

        //TOOLBAR
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Session In Progress");
        setSupportActionBar(toolbar);

        currentSession = new Session();
        currentSession.StartTracking();

        database = AppDatabase.getDatabase(getApplicationContext());
        tempTrickList = new ArrayList<Trick>();
        sessionIDs = new ArrayList<String>();

        trickRecyclerView = (RecyclerView) findViewById(R.id.trickRecyclerView);
        trickRecyclerView.setHasFixedSize(true);
        trickLayoutManager = new LinearLayoutManager(this);
        trickRecyclerView.setLayoutManager(trickLayoutManager);
        trickAdapter = new TrickAdapter(tempTrickList);
        trickRecyclerView.setAdapter(trickAdapter);

        if (tempTrickList.size() == 0){
            trickRecyclerView.setVisibility(View.GONE);
            TextView tv = (TextView) findViewById(R.id.text_View);
            tv.setText("Click the plus button to start");
        }

        final Handler handler = new Handler();
        final Runnable rn = new Runnable() {
            @Override
            public void run() {
                if (currentSession.IsTracking()) {
                    toolbar.setTitle("Active Session: " + currentSession.EllapsedTime());
                    for(int i = 0; i < tempTrickList.size(); i++){
                        if(tempTrickList.get(i).IsTracking())
                            currentTrick = tempTrickList.get(i);

                    }
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(rn, 0);


        //ADD NEW TRICK BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the Builder class for convenient dialog construction
                final AlertDialog.Builder builder = new AlertDialog.Builder(CurrentSession.this);
                final LayoutInflater inflater = getLayoutInflater();
                final View dlgView = inflater.inflate(R.layout.add_trick_dialog, null);

                final TextView trickName = (EditText) dlgView.findViewById(R.id.trickNameField);
                builder.setView(dlgView)
                        .setMessage("Add New Trick")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Save to Trick List
                                tempTrickList.add(new Trick(trickName.getText().toString()));
                                trickAdapter.notifyDataSetChanged();
                                trickRecyclerView.setVisibility(View.VISIBLE);
                                TextView tv = (TextView) findViewById(R.id.text_View);
                                tv.setVisibility(View.GONE);

                                Intent intent = new Intent(getApplicationContext(), this.getClass());
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                //Notification myNotification = new Notification.Builder(getContext())

                                String channelId = "default_channel_id";
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
                                mBuilder.setSmallIcon(R.drawable.ic_healing_black_24dp);
                                mBuilder.addAction(R.drawable.ic_plus_1, "Trick Landed ",pendingIntent);
                                mBuilder.setContentTitle("Active Session");
                                mBuilder.setContentText("Trick: " + trickName.getText().toString());
                                mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                                mBuilder.setContentIntent(pendingIntent);
                                mBuilder.setAutoCancel(false);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(GenerateID(),mBuilder.build());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Cancel
                            }
                        });

                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        //senSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_current_session, menu);
        return true;
    }

    private int GenerateID() {
        //CREATE STATIC ID
        String ret = "";
        final String digits = "0123456789";
        final String alphanum = digits;
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int randIndex = Math.abs(random.nextInt()) % alphanum.length();
            char lett = alphanum.charAt(randIndex);
            ret += Character.toString(lett);
        }
        return Integer.parseInt(ret);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.end_session_item:
                // User chose the "Settings" item, show the app settings UI...
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Add the buttons
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Are you sure you want to end your Session?")
                        .setTitle("End Session");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        ArrayList<String> trickIDs = new ArrayList<String>();

                        for (Trick t:tempTrickList) {
                            t.PauseTracking();
                            trickIDs.add(t.GetID());
                        }
                        currentSession.PauseTracking();


                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                        // Create Session
                        final Map<String, Object> session = new HashMap<>();
                        session.put("date", currentSession.GetDate().toString());
                        session.put("id", currentSession.GetID());
                        session.put("totalTimeFormatted", currentSession.EllapsedTime());
                        session.put("uID", user.getUid());

                        final ProgressDialog progressDialog = ProgressDialog.show(CurrentSession.this, "",
                                "Saving Session...", true);

                        ArrayList<Map<String, Object>> trickList = new ArrayList<Map<String, Object>>();

                        //Create Map for trick
                        for(Trick t:tempTrickList){
                            // Create a new user with a first and last name
                            Map<String, Object> trick = new HashMap<>();
                            trick.put("name", t.GetName());
                            trick.put("id", t.GetID());
                            trick.put("totalTimeFormatted", t.EllapsedTime());
                            trick.put("ratio", t.GetRatio());
                            trick.put("timesLanded", t.GetTimesLanded());
                            //Add to session
                            trickList.add(trick);
                        }

                        session.put("tricks", trickList);

                        //Add Session to Document
                        db.collection("Sessions")
                                .document(currentSession.GetID()).set(session)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Saved Session", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                        /*
                        database.sessionDAO().insertSession(currentSession);
                        System.out.println(database.sessionDAO().getSessions().size());
                        finish();
                        */
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    public class ActionReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getStringExtra("Trick Landed ");
            if (action.equals("Trick Landed ")) {
                preformAction1();
            }
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
        public void preformAction1(){
            currentTrick.PauseTracking();
        }
    }
}

    /*
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long currTime = System.currentTimeMillis();

            if((currTime - lastUpdate) > 250){
                long diffTime = (currTime - lastUpdate);
                lastUpdate = currTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if(speed > SHAKE_THRESHOLD){
                    jumps++;
                    TextView tv = (TextView) findViewById(R.id.text_View);
                    tv.setText(Integer.toString(jumps));
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }*/
