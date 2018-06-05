package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TrickTracker extends AppCompatActivity {

    Trick currentTrick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trick_tracker);

        Intent recieveintent = getIntent();
        Bundle extras = recieveintent.getExtras();
        currentTrick = new Trick(extras.getString("name"), extras.getString("dbID"));
        currentTrick.StartTracking();

        currentTrick.UpdateTime();
        final ConstraintLayout landedLayout = findViewById(R.id.landedLayout);
        final TextView currMinutes = findViewById(R.id.timeMinute);
        final TextView currSecs = findViewById(R.id.timeSecond);
        final TextView timesLanded = findViewById(R.id.timesLandedText);
        final CircleProgress progress = findViewById(R.id.trickProgress);

        timesLanded.setText(Integer.toString(currentTrick.GetTimesLanded()) + " " + currentTrick.GetName() + "s Landed");

        final Handler handler = new Handler();
        final Runnable rn = new Runnable() {
            @Override
            public void run() {
                if (currentTrick.IsTracking()) {
                    currentTrick.UpdateTime();
                    int mins = Double.valueOf(currentTrick.GetMinutesTracked()).intValue();
                    currMinutes.setText(Integer.toString(mins));
                    int secs = Double.valueOf(currentTrick.GetSecondsTracked()).intValue();
                    currSecs.setText(Integer.toString(secs));
                    int ratio = Double.valueOf(currentTrick.GetRatio() * 100).intValue();
                    if(ratio < 0){
                        ratio = 0;
                    }else if(ratio > 100){
                        ratio = 100;
                    }
                    progress.setProgress(ratio);
                    timesLanded.setText(Integer.toString(currentTrick.GetTimesLanded()) + " " + currentTrick.GetName() + "s Landed");
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(rn, 0);

        Intent intenttrick = new Intent(this, ActionReceiver.class);
        Intent intentSession = new Intent(this, this.getClass());
        intentSession.putExtras(extras);
        intentSession.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intenttrick, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingSessionIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentSession, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "default_channel_id";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        mBuilder.setSmallIcon(R.drawable.ic_healing_black_24dp);
        mBuilder.setContentTitle("Practicing Trick");
        mBuilder.addAction(R.drawable.ic_plus_1, "Landed", pendingIntent);
        mBuilder.setContentText("Trick: " + currentTrick.GetName());
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setContentIntent(pendingSessionIntent);
        mBuilder.setAutoCancel(false);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(channelId, "SkateTracker", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(channelId);
        }

        notificationManager.notify(1742142, mBuilder.build());

        landedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTrick.IncrementTimesLanded();
                timesLanded.setText(Integer.toString(currentTrick.GetTimesLanded()) + " " + currentTrick.GetName() + "s Landed");
                int ratio = Double.valueOf(currentTrick.GetRatio() * 100).intValue();
                if(ratio < 0){
                    ratio = 0;
                }else if(ratio > 100){
                    ratio = 100;
                }
                progress.setProgress(ratio);
            }
        });

        FloatingActionButton stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTrick.IsTracking()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TrickTracker.this);
                    // Add the buttons
                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Are you sure you want to stop practicing?")
                            .setTitle("Stop Practicing");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //--FIREBASE STUFF--//
                            currentTrick.PauseTracking();
                            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.cancelAll();
                            final FirebaseFirestore db = FirebaseFirestore.getInstance();
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                final CollectionReference colRef = db.collection("users").document(user.getUid()).collection("tricks");
                                DocumentReference currDoc = colRef.document(currentTrick.GetDBID().toLowerCase());
                                currDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
                                            //----IF TRICK ALREADY EXISTS----//
                                            if (document.exists()) {
                                                Map<String, Object> existingTrick = document.getData();

                                                //Create Trick for User Object
                                                final Map<String, Object> userTrick = new HashMap<>();
                                                Long landings = (Long) (existingTrick.get("totalLandings"));
                                                if (landings != null)
                                                    userTrick.put("totalLandings", landings.intValue() + currentTrick.GetTimesLanded());
                                                else
                                                    userTrick.put("totalLandings", currentTrick.GetTimesLanded());

                                                //Get Current Sessions, Create Updated Sessions
                                                ArrayList<Map<String, Object>> currentTricks = (ArrayList<Map<String, Object>>) existingTrick.get("sessions");
                                                ArrayList<Map<String, Object>> updatedTricks = new ArrayList<Map<String, Object>>();
                                                double ratio = 0.0;

                                                if (currentTricks != null) {
                                                    //Add Current to Updated
                                                    for (Map<String, Object> currTrick : currentTricks) {
                                                        updatedTricks.add(currTrick);
                                                        double trickRatio = (double) currTrick.get("ratio");
                                                        ratio += trickRatio;
                                                    }
                                                }

                                                //Create Current Trick Session
                                                Map<String, Object> userTrickSesh = new HashMap<>();
                                                userTrickSesh.put("date", date);
                                                userTrickSesh.put("ratio", currentTrick.GetRatio());
                                                userTrickSesh.put("timeSpent", currentTrick.GetTotalSecondsTracked());
                                                userTrickSesh.put("totalLandings", currentTrick.GetTimesLanded());
                                                userTrickSesh.put("id", currentTrick.GetID());

                                                ratio += currentTrick.GetRatio();
                                                ratio = ratio / (currentTricks.size() + 1);

                                                //Add To Trick Object
                                                updatedTricks.add(userTrickSesh);
                                                userTrick.put("avgRatio", ratio);
                                                userTrick.put("name", currentTrick.GetName().toString().toUpperCase());
                                                userTrick.put("dbID", currentTrick.GetDBID().toLowerCase());
                                                userTrick.put("sessions", updatedTricks);

                                                //Add to User Object
                                                db.collection("users").document(user.getUid())
                                                        .collection("tricks")
                                                        .document(currentTrick.GetDBID().toLowerCase())
                                                        .set(userTrick)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //------------------------------------------//
                                                                //------------CHECK FOR CHALLENGES----------//
                                                                //------------------------------------------//
                                                                DocumentReference docRef = db.collection("users").document(user.getUid());
                                                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        Query query = db.collection("challenges");
                                                                        System.out.println("CHECKING STARTED");
                                                                        if (task.isSuccessful()) {
                                                                            DocumentSnapshot document = task.getResult();
                                                                            if (document.exists()) {
                                                                                final Map<String, Object> userData = document.getData();
                                                                                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                                                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                                                                                        final List<String> userChallenges = (List<String>) userData.get("challenges");
                                                                                        for (DocumentSnapshot doc : docs) {
                                                                                            Map<String, Object> data = doc.getData();

                                                                                            //--Trick Landings--//
                                                                                            if (currentTrick.GetTimesLanded() >= 1) {
                                                                                                if (data.get("type").equals("land") && (long) data.get("requirement") <= (int)userTrick.get("totalLandings")) {
                                                                                                    if (userChallenges != null && !userChallenges.contains(data.get("id"))) {
                                                                                                        userChallenges.add((String) (data.get("id")));
                                                                                                        Toast.makeText(getApplicationContext(), "New Achievement: " + data.get("name").toString(), Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            if (currentTrick.GetRatio() > 0.0) {
                                                                                                if (data.get("type").equals("ratio") && (long) data.get("requirement") <= Double.valueOf((double)userTrick.get("avgRatio")).longValue()) {
                                                                                                    if (userChallenges != null && !userChallenges.contains(data.get("id"))) {
                                                                                                        userChallenges.add((String) (data.get("id")));
                                                                                                        Toast.makeText(getApplicationContext(), "New Achievement: " + data.get("name").toString(), Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        db.collection("users").document(user.getUid()).update("challenges", userChallenges).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                CheckAchievements(userChallenges, (List<String>)(userData.get("achievements")));
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                db.collection("users")
                                                        .document(user.getUid()).update("recent_trick", currentTrick.GetDBID())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid){
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                //----IF TRICK DOES NOT EXIST----//
                                            } else {
                                                //Create Trick for User Object
                                                final Map<String, Object> userTrick = new HashMap<>();
                                                userTrick.put("totalLandings", currentTrick.GetTimesLanded());

                                                //Get Current Sessions, Create Updated Sessions
                                                ArrayList<Map<String, Object>> updatedTricks = new ArrayList<Map<String, Object>>();

                                                //Create Current Trick Session
                                                Map<String, Object> userTrickSesh = new HashMap<>();
                                                userTrickSesh.put("date", date);
                                                userTrickSesh.put("ratio", currentTrick.GetRatio());
                                                userTrickSesh.put("timeSpent", currentTrick.GetTotalSecondsTracked());
                                                userTrickSesh.put("id", currentTrick.GetID());
                                                userTrickSesh.put("totalLandings", currentTrick.GetTimesLanded());

                                                //Add To Trick Object
                                                updatedTricks.add(userTrickSesh);
                                                userTrick.put("sessions", updatedTricks);
                                                userTrick.put("name", currentTrick.GetName().toString().toUpperCase());
                                                userTrick.put("dbID", currentTrick.GetDBID().toLowerCase());

                                                double ratio = (currentTrick.GetRatio() / updatedTricks.size());
                                                userTrick.put("avgRatio", ratio);

                                                //Add to User Object
                                                db.collection("users").document(user.getUid())
                                                        .collection("tricks")
                                                        .document(currentTrick.GetDBID().toLowerCase())
                                                        .set(userTrick)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //------------------------------------------//
                                                                //------------CHECK FOR CHALLENGES----------//
                                                                //------------------------------------------//
                                                                DocumentReference docRef = db.collection("users").document(user.getUid());
                                                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        Query query = db.collection("challenges");
                                                                        System.out.println("CHECKING STARTED");
                                                                        if (task.isSuccessful()) {
                                                                            DocumentSnapshot document = task.getResult();
                                                                            if (document.exists()) {
                                                                                final Map<String, Object> userData = document.getData();
                                                                                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                                                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                                                                                        final List<String> userChallenges = (List<String>) userData.get("challenges");
                                                                                            for (DocumentSnapshot doc : docs) {
                                                                                                Map<String, Object> data = doc.getData();

                                                                                                //--Trick Landings--//
                                                                                                if (currentTrick.GetTimesLanded() >= 1) {
                                                                                                    if (data.get("type").equals("land") && (long) data.get("requirement") <= (int)userTrick.get("totalLandings")) {
                                                                                                        if (userChallenges != null && !userChallenges.contains(data.get("id"))) {
                                                                                                            userChallenges.add((String) (data.get("id")));
                                                                                                            Toast.makeText(getApplicationContext(), "New Achievement: " + data.get("name").toString(), Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                if (currentTrick.GetRatio() > 0.0) {
                                                                                                    if (data.get("type").equals("ratio") && (long) data.get("requirement") <= Double.valueOf((double)userTrick.get("avgRatio")).longValue()) {
                                                                                                        if (userChallenges != null && !userChallenges.contains(data.get("id"))) {
                                                                                                            userChallenges.add((String) (data.get("id")));
                                                                                                            Toast.makeText(getApplicationContext(), "New Achievement: " + data.get("name").toString(), Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        db.collection("users").document(user.getUid()).update("challenges", userChallenges).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                CheckAchievements(userChallenges, (List<String>)(userData.get("achievements")));
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                db.collection("users")
                                                        .document(user.getUid()).update("recent_trick", currentTrick.GetDBID())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid){
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });

    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            onMessageReceived();
        }
    };

    private void onMessageReceived() {
        currentTrick.IncrementTimesLanded();
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(myReceiver, new IntentFilter("trick_landed"));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private void CheckAchievements(final List<String> challenges, final List<String> userAchievements){
        //--FIREBASE STUFF--//
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        Query query = db.collection("achievements");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                List<String> updatedAchievements = userAchievements;
                for (DocumentSnapshot doc : docs) {
                    Map<String, Object> data = doc.getData();
                    List<String> reqChallenges = (List<String>)(data.get("challenges"));
                    int found = 0;
                    for(String c : reqChallenges){
                        if(challenges.contains(c)){
                            found++;
                        }else{
                            break;
                        }
                    }
                    if(updatedAchievements == null){
                        updatedAchievements = new ArrayList<String>();
                    }
                    if(found >= reqChallenges.size() && !updatedAchievements.contains(data.get("id").toString())){
                        updatedAchievements.add(doc.getData().get("id").toString());
                    }
                }
                db.collection("users").document(user.getUid()).update("achievements", updatedAchievements).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.cancelAll();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
