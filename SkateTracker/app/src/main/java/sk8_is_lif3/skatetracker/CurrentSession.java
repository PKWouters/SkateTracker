package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.RenderScript;
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
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sk8_is_lif3.skatetracker.database.AppDatabase;

public class CurrentSession extends AppCompatActivity{


    private RecyclerView trickRecyclerView;
    private RecyclerView.Adapter trickAdapter;
    private RecyclerView.LayoutManager trickLayoutManager;
    private AppDatabase database;
    List<Trick> tempTrickList;
    Session currentSession;
    Trick currentTrick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_session);

        //TOOLBAR
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Session In Progress");
        setSupportActionBar(toolbar);

        currentSession = new Session();
        currentSession.StartTracking();

        database = AppDatabase.getDatabase(getApplicationContext());
        tempTrickList = new ArrayList<Trick>();

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

                                Intent intenttrick = new Intent(getApplicationContext(), ActionReceiver.class);
                                //intenttrick.setAction("Add Trick");
                                intenttrick.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                //PendingIntent trickpend = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                                PendingIntent trickpendclick = PendingIntent.getBroadcast(getApplicationContext(), 0, intenttrick, PendingIntent.FLAG_UPDATE_CURRENT);

                                //Notification myNotification = new Notification.Builder(getContext())

                                String channelId = "default_channel_id";
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
                                mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
                                mBuilder.setSmallIcon(R.drawable.ic_healing_black_24dp);
                                mBuilder.setContentTitle("Active Session");
                                mBuilder.addAction(R.drawable.ic_plus_1, "Add Trick", trickpendclick);
                                mBuilder.setContentText("Trick: " + trickName.getText().toString());
                                mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                                mBuilder.setContentIntent(pendingIntent);
                                mBuilder.setAutoCancel(false);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    NotificationChannel mChannel = new NotificationChannel("chanel id", "skate notification",NotificationManager.IMPORTANCE_HIGH);

                                    mNotificationManager.createNotificationChannel(mChannel);
                                }

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
                        for (Trick t:tempTrickList) {
                            t.PauseTracking();
                            currentSession.AddTrick(t);
                        }
                        currentSession.PauseTracking();
                        database.sessionDAO().insertSession(currentSession);
                        System.out.println(database.sessionDAO().getSessions().size());
                        finish();
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
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            onMessageReceived();
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, new IntentFilter("Add Trick"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    private void onMessageReceived() {

        currentTrick.IncrementTimesLanded();
    }

}