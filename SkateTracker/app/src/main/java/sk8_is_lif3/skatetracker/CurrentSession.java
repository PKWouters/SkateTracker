package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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


public class CurrentSession extends AppCompatActivity /*implements SensorEventListener*/ {


    private RecyclerView trickRecyclerView;
    private TrickAdapter tAdapt;
    private RecyclerView.Adapter trickAdapter;
    private RecyclerView.LayoutManager trickLayoutManager;
    List<Trick> tempTrickList;
    ArrayList<String> trickList;
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

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("tricks").document("trick_LIST");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        trickList = (ArrayList<String>)(data.get("list"));
                    } else {
                        Toast.makeText(getApplicationContext(), "Could Not Load Trick List", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
        });

        currentSession = new Session();
        currentSession.StartTracking();

        tempTrickList = new ArrayList<Trick>();
        sessionIDs = new ArrayList<String>();

        trickRecyclerView = (RecyclerView) findViewById(R.id.trickRecyclerView);
        trickRecyclerView.setHasFixedSize(true);
        trickLayoutManager = new LinearLayoutManager(this);
        trickRecyclerView.setLayoutManager(trickLayoutManager);
        tAdapt = new TrickAdapter(tempTrickList);
        trickAdapter = (RecyclerView.Adapter) tAdapt;
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

                        .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //Search Tricks With Names
                                String tName = trickName.getText().toString().replaceAll("\\s+","_").toLowerCase();
                                final ArrayList<String> foundTrickIDs = new ArrayList<>();
                                final CharSequence[] foundTrickNames;
                                for(int i = 0; i < trickList.size(); i++){
                                    if(trickList.get(i).contains(tName) && foundTrickIDs.size() < 5){
                                        foundTrickIDs.add(trickList.get(i).toString());
                                    }
                                    if(foundTrickIDs.size() >= 5){
                                        break;
                                    }
                                }
                                foundTrickNames = new CharSequence[foundTrickIDs.size()];
                                for(int i = 0; i < foundTrickIDs.size(); i++){
                                    foundTrickNames[i] = foundTrickIDs.get(i).substring(6).replaceAll("_"," ").toUpperCase();
                                }

                                final AlertDialog.Builder choiceBuilder = new AlertDialog.Builder(CurrentSession.this);
                                choiceBuilder.setTitle("Choose A Trick");
                                if(foundTrickNames.length > 0) {
                                    choiceBuilder.setItems(foundTrickNames, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Save to Trick List
                                            tempTrickList.add(new Trick(foundTrickNames[which].toString(), foundTrickIDs.get(which)));
                                            trickAdapter.notifyDataSetChanged();
                                            trickRecyclerView.setVisibility(View.VISIBLE);
                                            TextView tv = (TextView) findViewById(R.id.text_View);
                                            tv.setVisibility(View.GONE);
                                        }
                                    }).setNeutralButton("Add as Custom", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Save to Trick List
                                            tempTrickList.add(new Trick(trickName.getText().toString().toUpperCase(), "custom_" + trickName.getText().toString().replaceAll("\\s+","_").toLowerCase()));
                                            trickAdapter.notifyDataSetChanged();
                                            trickRecyclerView.setVisibility(View.VISIBLE);
                                            TextView tv = (TextView) findViewById(R.id.text_View);
                                            tv.setVisibility(View.GONE);
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                }else {
                                    choiceBuilder.setMessage("No tricks containing \"" + trickName.getText().toString() + "\" found. You can still add this trick as a Custom Trick.")
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setNeutralButton("Add as Custom", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Save to Trick List
                                                    tempTrickList.add(new Trick(trickName.getText().toString().toUpperCase(), "custom_" + trickName.getText().toString().replaceAll("\\s+","_").toLowerCase()));
                                                    trickAdapter.notifyDataSetChanged();
                                                    trickRecyclerView.setVisibility(View.VISIBLE);
                                                    TextView tv = (TextView) findViewById(R.id.text_View);
                                                    tv.setVisibility(View.GONE);
                                                }
                                            });
                                }
                                choiceBuilder.create();
                                choiceBuilder.show();


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
        onResume();
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
    public void onBackPressed() {

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
                        // Use the Builder class for convenient dialog construction
                        final AlertDialog.Builder sessionNameBuilder = new AlertDialog.Builder(CurrentSession.this);
                        final LayoutInflater inflater = getLayoutInflater();
                        final View dlgView = inflater.inflate(R.layout.name_session_dialog, null);

                        final TextView sessionNameField = (EditText) dlgView.findViewById(R.id.trickNameField);
                        sessionNameBuilder.setView(dlgView)
                                .setMessage("Name Your Session")

                                //---------------------------------//
                                //-------------WITH NAME-----------//
                                //---------------------------------//
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        //Init Stuff
                                        final String sessionName = sessionNameField.getText().toString();
                                        ArrayList<String> trickIDs = new ArrayList<String>();

                                        //Pause All Tricks
                                        for (Trick t : tempTrickList) {
                                            t.PauseTracking();
                                            trickIDs.add(t.GetID());
                                        }
                                        currentSession.PauseTracking();


                                        //--FIREBASE STUFF--//
                                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        // Create Session
                                        final Map<String, Object> session = new HashMap<>();
                                        session.put("date", currentSession.GetDate().toString());
                                        session.put("id", currentSession.GetID());
                                        session.put("totalTimeFormatted", currentSession.EllapsedTime());
                                        session.put("name", sessionName);

                                        final ProgressDialog progressDialog = ProgressDialog.show(CurrentSession.this, "",
                                                "Saving Session...", true);

                                        ArrayList<Map<String, Object>> trickList = new ArrayList<Map<String, Object>>();
                                        final CollectionReference colRef = db.collection("users").document(user.getUid()).collection("tricks");

                                        Random rand = new Random();
                                        int randIndex = rand.nextInt() % (tempTrickList.size());
                                        Trick randTrick = tempTrickList.get(randIndex);
                                        //Add Session to Document

                                        db.collection("users")
                                                .document(user.getUid()).update("recent_trick", randTrick.GetDBID())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });


                                        //Set Up Trick Objects
                                        for (final Trick t : tempTrickList) {
                                            // Create Trick for Session Object
                                            Map<String, Object> trick = new HashMap<>();
                                            trick.put("name", t.GetName());
                                            trick.put("id", t.GetID());
                                            trick.put("totalTimeFormatted", t.EllapsedTime());
                                            trick.put("ratio", t.GetRatio());
                                            trick.put("dbID", t.GetDBID().toLowerCase());
                                            trick.put("timesLanded", t.GetTimesLanded());
                                            trickList.add(trick);


                                            DocumentReference currDoc = colRef.document(t.GetDBID().toLowerCase());
                                            currDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        //----IF TRICK ALREADY EXISTS----//
                                                        if (document.exists()) {
                                                            Map<String, Object> existingTrick = document.getData();

                                                            //Create Trick for User Object
                                                            Map<String, Object> userTrick = new HashMap<>();
                                                            Long landings = (Long)(existingTrick.get("totalLandings"));
                                                            if(landings != null)
                                                                userTrick.put("totalLandings", landings.intValue()+t.GetTimesLanded());
                                                            else
                                                                userTrick.put("totalLandings", t.GetTimesLanded());

                                                            //Get Current Sessions, Create Updated Sessions
                                                            ArrayList<Map<String, Object>> currentTricks = (ArrayList<Map<String, Object>>)existingTrick.get("sessions");
                                                            ArrayList<Map<String, Object>> updatedTricks = new ArrayList<Map<String, Object>>();
                                                            double ratio = 0.0;

                                                            if(currentTricks != null) {
                                                                //Add Current to Updated
                                                                for (Map<String, Object> currTrick : currentTricks) {
                                                                    updatedTricks.add(currTrick);
                                                                    double trickRatio = (double)currTrick.get("ratio");
                                                                    ratio += trickRatio;
                                                                }
                                                            }

                                                            //Create Current Trick Session
                                                            Map<String, Object> userTrickSesh = new HashMap<>();
                                                            userTrickSesh.put("date", currentSession.GetDate().toString());
                                                            userTrickSesh.put("ratio", t.GetRatio());
                                                            userTrickSesh.put("timeSpent", t.GetTotalSecondsTracked());
                                                            userTrickSesh.put("totalLandings", t.GetTimesLanded());
                                                            userTrickSesh.put("id", t.GetID());

                                                            ratio += t.GetRatio();
                                                            ratio = ratio / (currentTricks.size()+1);

                                                            //Add To Trick Object
                                                            updatedTricks.add(userTrickSesh);
                                                            userTrick.put("avgRatio", ratio);
                                                            userTrick.put("name", t.GetName().toString().toUpperCase());
                                                            userTrick.put("dbID", t.GetDBID().toLowerCase());
                                                            userTrick.put("sessions", updatedTricks);

                                                            //Add to User Object
                                                            db.collection("users").document(user.getUid())
                                                                    .collection("tricks")
                                                                    .document(t.GetDBID().toLowerCase())
                                                                    .set(userTrick)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            //----IF TRICK DOES NOT EXIST----//
                                                        } else {
                                                            //Create Trick for User Object
                                                            Map<String, Object> userTrick = new HashMap<>();
                                                            userTrick.put("totalLandings", t.GetTimesLanded());

                                                            //Get Current Sessions, Create Updated Sessions
                                                            ArrayList<Map<String, Object>> updatedTricks = new ArrayList<Map<String, Object>>();

                                                            //Create Current Trick Session
                                                            Map<String, Object> userTrickSesh = new HashMap<>();
                                                            userTrickSesh.put("date", currentSession.GetDate().toString());
                                                            userTrickSesh.put("ratio", t.GetRatio());
                                                            userTrickSesh.put("timeSpent", t.GetTotalSecondsTracked());
                                                            userTrickSesh.put("id", t.GetID());
                                                            userTrickSesh.put("totalLandings", t.GetTimesLanded());

                                                            //Add To Trick Object
                                                            updatedTricks.add(userTrickSesh);
                                                            userTrick.put("sessions", updatedTricks);
                                                            userTrick.put("name", t.GetName().toString().toUpperCase());
                                                            userTrick.put("dbID", t.GetDBID().toLowerCase());

                                                            double ratio = (t.GetRatio()/updatedTricks.size());
                                                            userTrick.put("avgRatio", ratio);

                                                            //Add to User Object
                                                            db.collection("users").document(user.getUid())
                                                                    .collection("tricks")
                                                                    .document(t.GetDBID().toLowerCase())
                                                                    .set(userTrick)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                    }
                                                }
                                            });


                                        }

                                        session.put("tricks", trickList);



                                        //Add Session to Document
                                        db.collection("users")
                                                .document(user.getUid()).collection("sessions").document(currentSession.GetID()).set(session)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressDialog.dismiss();
                                                        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                                        mNotificationManager.cancelAll();
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
                                    }
                                });
                        //---------------------------------//
                        //--------------NO NAME------------//
                        //---------------------------------//
                        sessionNameBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Init Stuff
                                ArrayList<String> trickIDs = new ArrayList<String>();

                                //Pause All Tricks
                                for (Trick t : tempTrickList) {
                                    t.PauseTracking();
                                    trickIDs.add(t.GetID());
                                }
                                currentSession.PauseTracking();


                                //--FIREBASE STUFF--//
                                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                // Create Session
                                final Map<String, Object> session = new HashMap<>();
                                session.put("date", currentSession.GetDate().toString());
                                session.put("id", currentSession.GetID());
                                session.put("totalTimeFormatted", currentSession.EllapsedTime());
                                session.put("name", currentSession.GetDate().toString());

                                final ProgressDialog progressDialog = ProgressDialog.show(CurrentSession.this, "",
                                        "Saving Session...", true);

                                ArrayList<Map<String, Object>> trickList = new ArrayList<Map<String, Object>>();
                                final CollectionReference colRef = db.collection("users").document(user.getUid()).collection("tricks");
                                Random rand = new Random();
                                if(tempTrickList.size() > 0) {
                                    int randIndex = rand.nextInt() % (tempTrickList.size());
                                    Trick randTrick = tempTrickList.get(randIndex);
                                    //Add Session to Document
                                    db.collection("users")
                                            .document(user.getUid()).update("recent_trick", randTrick.GetDBID())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });

                                }
                                //Set Up Trick Objects
                                for (final Trick t : tempTrickList) {
                                    // Create Trick for Session Object
                                    Map<String, Object> trick = new HashMap<>();
                                    trick.put("name", t.GetName());
                                    trick.put("id", t.GetID());
                                    trick.put("totalTimeFormatted", t.EllapsedTime());
                                    trick.put("ratio", t.GetRatio());
                                    trick.put("dbID", t.GetDBID().toLowerCase());
                                    trick.put("timesLanded", t.GetTimesLanded());
                                    trickList.add(trick);


                                    DocumentReference currDoc = colRef.document(t.GetDBID().toLowerCase());
                                    currDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                //----IF TRICK ALREADY EXISTS----//
                                                if (document.exists()) {
                                                    Map<String, Object> existingTrick = document.getData();

                                                    //Create Trick for User Object
                                                    Map<String, Object> userTrick = new HashMap<>();
                                                    Long landings = (Long)(existingTrick.get("totalLandings"));
                                                    if(landings != null)
                                                        userTrick.put("totalLandings", landings.intValue()+t.GetTimesLanded());
                                                    else
                                                        userTrick.put("totalLandings", t.GetTimesLanded());

                                                    //Get Current Sessions, Create Updated Sessions
                                                    ArrayList<Map<String, Object>> currentTricks = (ArrayList<Map<String, Object>>)existingTrick.get("sessions");
                                                    ArrayList<Map<String, Object>> updatedTricks = new ArrayList<Map<String, Object>>();
                                                    double ratio = 0.0;

                                                    if(currentTricks != null) {
                                                        //Add Current to Updated
                                                        for (Map<String, Object> currTrick : currentTricks) {
                                                            updatedTricks.add(currTrick);
                                                            double trickRatio = (double)currTrick.get("ratio");
                                                            ratio += trickRatio;
                                                        }
                                                    }

                                                    //Create Current Trick Session
                                                    Map<String, Object> userTrickSesh = new HashMap<>();
                                                    userTrickSesh.put("date", currentSession.GetDate().toString());
                                                    userTrickSesh.put("ratio", t.GetRatio());
                                                    userTrickSesh.put("timeSpent", t.GetTotalSecondsTracked());
                                                    userTrickSesh.put("totalLandings", t.GetTimesLanded());
                                                    userTrickSesh.put("id", t.GetID());

                                                    ratio += t.GetRatio();
                                                    ratio = ratio / (currentTricks.size()+1);

                                                    //Add To Trick Object
                                                    updatedTricks.add(userTrickSesh);
                                                    userTrick.put("avgRatio", ratio);
                                                    userTrick.put("name", t.GetName().toString().toUpperCase());
                                                    userTrick.put("dbID", t.GetDBID().toLowerCase());
                                                    userTrick.put("sessions", updatedTricks);

                                                    //Add to User Object
                                                    db.collection("users").document(user.getUid())
                                                            .collection("tricks")
                                                            .document(t.GetDBID().toLowerCase())
                                                            .set(userTrick)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    //----IF TRICK DOES NOT EXIST----//
                                                } else {
                                                    //Create Trick for User Object
                                                    Map<String, Object> userTrick = new HashMap<>();
                                                    userTrick.put("totalLandings", t.GetTimesLanded());

                                                    //Get Current Sessions, Create Updated Sessions
                                                    ArrayList<Map<String, Object>> updatedTricks = new ArrayList<Map<String, Object>>();

                                                    //Create Current Trick Session
                                                    Map<String, Object> userTrickSesh = new HashMap<>();
                                                    userTrickSesh.put("date", currentSession.GetDate().toString());
                                                    userTrickSesh.put("ratio", t.GetRatio());
                                                    userTrickSesh.put("timeSpent", t.GetTotalSecondsTracked());
                                                    userTrickSesh.put("id", t.GetID());
                                                    userTrickSesh.put("totalLandings", t.GetTimesLanded());

                                                    //Add To Trick Object
                                                    updatedTricks.add(userTrickSesh);
                                                    userTrick.put("sessions", updatedTricks);
                                                    userTrick.put("name", t.GetName().toString().toUpperCase());
                                                    userTrick.put("dbID", t.GetDBID().toLowerCase());

                                                    double ratio = (t.GetRatio()/updatedTricks.size());
                                                    userTrick.put("avgRatio", ratio);

                                                    //Add to User Object
                                                    db.collection("users").document(user.getUid())
                                                            .collection("tricks")
                                                            .document(t.GetDBID().toLowerCase())
                                                            .set(userTrick)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                            }
                                        }
                                    });


                                }

                                session.put("tricks", trickList);

                                //Add Session to Document
                                db.collection("users")
                                        .document(user.getUid()).collection("sessions").document(currentSession.GetID()).set(session)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                                mNotificationManager.cancelAll();
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
                            }
                        });

                        // Create the AlertDialog object and return it
                        sessionNameBuilder.create();
                        sessionNameBuilder.show();
                        /*
                        database.sessionDAO().insertSession(currentSession);
                        NotificationManager nM = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        nM.cancelAll();
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
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            onMessageReceived();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        currentTrick = tAdapt.GetCurrentTrick();
        registerReceiver(myReceiver, new IntentFilter("trick_landed"));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        tAdapt.SetCurrentTrick(null);
        unregisterReceiver(myReceiver);
    }

    private void onMessageReceived() {
        if(currentTrick != null)
            currentTrick.IncrementTimesLanded();
    }

}
