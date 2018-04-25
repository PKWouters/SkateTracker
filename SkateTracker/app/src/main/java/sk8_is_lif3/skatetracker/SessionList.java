package sk8_is_lif3.skatetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadSystemException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Map;

import sk8_is_lif3.skatetracker.database.AppDatabase;
import sk8_is_lif3.skatetracker.transitions.SessionNameTransition;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SessionList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SessionList extends Fragment {

    private static final String TAG = "SessionList: ";
    private RecyclerView sessionRecyclerView;
    private RecyclerView.LayoutManager sessionLayoutManager;
    private FirebaseUser user;
    private AppDatabase database;
    List<String> sessionList;
    private FirestoreRecyclerAdapter<SessionToDisplay, SessionViewHolder> adapter;

    public SessionList() {
        // Required empty public constructor
    }
    public static SessionList newInstance(String param1, String param2) {
        SessionList fragment = new SessionList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        sessionRecyclerView = getView().findViewById(R.id.sessionsRecyclerView);
        sessionRecyclerView.setHasFixedSize(true);
        sessionLayoutManager = new LinearLayoutManager(getContext());
        sessionRecyclerView.setLayoutManager(sessionLayoutManager);
        sessionRecyclerView.setAdapter(adapter);

        final FloatingActionButton floatingActionButton = (FloatingActionButton)getView().findViewById(R.id.newSessionFab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CurrentSession.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                intent.putExtra("session time", 0);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().startActivity(intent);
            }
        });
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.setTitle("My Sessions");
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);

        if (sessionList.size() == 0){
            TextView tv = (TextView) getView().findViewById(R.id.text_View);
            tv.setText("Click the plus button to start");
        }

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
    public void onPause(){
        super.onPause();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionList = new ArrayList<String>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {

            Query query = db.collection("Sessions").whereEqualTo("uID", user.getUid());

            FirestoreRecyclerOptions<SessionToDisplay> options = new FirestoreRecyclerOptions.Builder<SessionToDisplay>()
                    .setQuery(query, SessionToDisplay.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<SessionToDisplay, SessionViewHolder>(options) {

                private int _expandedPosition = -1;
                private int _previousExpandedPosition = -1;

                @NonNull
                @Override
                public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    // create a new view
                    View v = (View) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.session_card_layout, parent, false);
                    final SessionViewHolder vh = new SessionViewHolder(v);

                    return vh;
                }

                @Override
                protected void onBindViewHolder(@NonNull final SessionViewHolder holder, final int position, @NonNull final SessionToDisplay model) {
                    final CardView cardView = holder.itemView.findViewById(R.id.card_view);
                    holder.sessionNameView.setText(model.getDate() + " - " + model.getName());
                    holder.sessionNameView.setMaxLines(2);
                    holder.sessionNameView.setTextColor(Color.WHITE);
                    holder.removeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setMessage("Are you sure you want to remove this Session?")
                                    .setTitle("Remove Session")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            TransitionManager.beginDelayedTransition(cardView);
                                            db.collection("Sessions").document(model.getId())
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Cancel
                                        }
                                    });

                            // Create the AlertDialog object and return it
                            builder.create();
                            builder.show();
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            SessionDetailFragment nextFrag = new SessionDetailFragment(model.getDate() + " - " + model.getName(), model.getTotalTimeFormatted(), model.getTricks());

                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .addSharedElement(holder.sessionNameView, "sessionNameTransition")
                                    .replace(R.id.fragment, nextFrag,"SessionDetailFragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                }

                private double GetGCDDen(double n) {
                    String s = String.format("%.1f", n);
                    int digitsDec = s.length() - 1 - s.indexOf('.');
                    int denom = 1;
                    for (int i = 0; i < digitsDec; i++) {
                        n *= 10;
                        denom *= 10;
                    }

                    int num = (int) Math.round(n);
                    double g = gcd(num, denom);
                    return (denom / g);
                }

                private double GetGCDNum(double n) {
                    String s = String.format("%.1f", n);
                    int digitsDec = s.length() - 1 - s.indexOf('.');
                    int denom = 1;
                    for (int i = 0; i < digitsDec; i++) {
                        n *= 10;
                        denom *= 10;
                    }

                    int num = (int) Math.round(n);
                    double g = gcd(num, denom);
                    return (num / g);
                }

                private double gcd(int x, int y) {
                    int r = x % y;
                    while (r != 0) {
                        x = y;
                        y = r;
                        r = x % y;
                    }
                    return y;
                }

            };
        }
        onResume();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_session_list, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(adapter != null)
            adapter.startListening();

    }

    private class SessionViewHolder extends RecyclerView.ViewHolder {
        private View view;

        // each data item is just a string in this case
        public TextView sessionNameView;
        public View itemView;
        public ImageView removeButton;
        public SessionViewHolder(View v) {
            super(v);
            itemView = v;
            sessionNameView = v.findViewById(R.id.sessionName);
            removeButton = v.findViewById(R.id.removeButton);
        }
    }

    public ArrayList<String> ConvertJSONtoList(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

}
