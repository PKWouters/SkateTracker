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
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                //PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0 , intent, 0);

                //Notification myNotification = new Notification.Builder(getContext())
                /*
                String channelId = "default_channel_id";
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), channelId);
                    mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
                    mBuilder.setContentTitle("The Session");
                    mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                notificationManager.notify(GenerateID(),mBuilder.build());



                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),channelId) ;
                builder.setSmallIcon((R.drawable.ic_notifications_black_24dp));
                builder.setContentTitle(("The session"));
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                builder.setAutoCancel(true);


                Intent intent = new Intent(getContext(), CurrentSession.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivityForResult(intent,0);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                stackBuilder.addParentStack(MainNavigationActivity.class);
                stackBuilder.addNextIntentWithParentStack(intent);

                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(pendingIntent);

                NotificationManagerCompat NM = NotificationManagerCompat.from(getContext());
                NM.notify(GenerateID(),builder.build());
                */
            }
        });
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.setTitle("My Sessions");
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);

        if (sessionList.size() == 0){
            //sessionRecyclerView.setVisibility(View.GONE);
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
                    final boolean isExpanded = position == _expandedPosition;
                    holder.sessionNameView.setText(model.getDate() + " - " + model.getName());
                    holder.sessionNameView.setMaxLines(isExpanded ? 2 : 1);
                    holder.totalTimeView.setText(model.getTotalTimeFormatted());
                    holder.sessionNameView.setTextColor(Color.WHITE);
                    holder.totalTimeView.setTextColor(Color.WHITE);
                    holder.totalTricksView.setTextColor(Color.WHITE);
                    holder.removeButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    holder.totalTimeView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    holder.totalTricksView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    holder.barChart.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    cardView.setCardElevation(isExpanded ? 6 : 0);
                    cardView.setActivated(isExpanded);
                    ArrayList<Map<String, Object>> tricks = (ArrayList<Map<String, Object>>) model.getTricks();
                    if (tricks != null)
                        holder.totalTricksView.setText(tricks.size() + " tricks practiced");
                    if (isExpanded)
                        _previousExpandedPosition = position;

                    if (model.getTricks() != null) {
                        //--------------Graph Stuff---------------//

                        final String[] trickNames = new String[tricks.size()];
                        for (int i = 0; i < trickNames.length; ++i) {
                            trickNames[i] = String.valueOf(tricks.get(i).get("name") + System.getProperty("line.separator") + tricks.get(i).get("totalTimeFormatted") + System.getProperty("line.separator") + tricks.get(i).get("timesLanded") + " Successful Attempts");
                        }

                        List<BarEntry> entries = new ArrayList<BarEntry>();
                        int[] colors = new int[tricks.size()];
                        for (int i = 0; i < tricks.size(); ++i) {
                            double ratio = Double.parseDouble(tricks.get(i).get("ratio").toString());
                            if (ratio < 1.0) {
                                entries.add(new BarEntry(i, (float) (ratio)));
                                colors[i] = Color.RED;
                            } else {
                                entries.add(new BarEntry(i, (float) (ratio)));
                                colors[i] = Color.GREEN;
                            }
                        }

                        BarDataSet dataSet = new BarDataSet(entries, "Tricks"); // add entries to dataset
                        Description d = new Description();
                        d.setText("");
                        holder.barChart.setDescription(d);
                        holder.barChart.setVisibleXRangeMaximum(4.25f);
                        holder.barChart.setScaleEnabled(false);
                        dataSet.setLabel("Successful Landings/Minutes");
                        dataSet.setColors(colors);
                        dataSet.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                return String.valueOf((int) (GetGCDNum(value)) + " / " + (int) (GetGCDDen(value)));
                            }
                        });
                        XAxis xAxis = holder.barChart.getXAxis();

                        int screenSize = holder.itemView.getResources().getConfiguration().screenLayout &
                                Configuration.SCREENLAYOUT_SIZE_MASK;
                        switch (screenSize) {
                            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                                xAxis.setTextSize(8f);
                                break;
                            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                                xAxis.setTextSize(4f);
                                break;
                            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                                xAxis.setTextSize(2f);
                                break;
                            default:

                        }

                        dataSet.setValueTextColor(Color.WHITE);

                        BarData lineData = new BarData(dataSet);
                        holder.barChart.setData(lineData);
                        holder.barChart.invalidate(); // refresh
                        holder.barChart.getLegend().setTextColor(Color.WHITE);
                        holder.barChart.getLegend().setForm(Legend.LegendForm.LINE);

                        IAxisValueFormatter xFormatter = new IAxisValueFormatter() {

                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                if ((int) (value) < trickNames.length)
                                    return trickNames[(int) value];
                                return "TRICK NOT FOUND";
                            }


                        };

                        IAxisValueFormatter yFormatter = new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                return Float.toString(value);
                            }
                        };

                        XAxisRenderer xRenderer = new XAxisRenderer(holder.barChart.getViewPortHandler(), xAxis, holder.barChart.getTransformer(YAxis.AxisDependency.LEFT)) {
                            @Override
                            protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
                                String lines[] = formattedLabel.split("\n");
                                for (int i = 0; i < lines.length; i++) {
                                    float vOffset = i * mAxisLabelPaint.getTextSize();
                                    Utils.drawXAxisValue(c, lines[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees);
                                }
                            }
                        };
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                        xAxis.setTextColor(Color.WHITE);
                        xAxis.setValueFormatter(xFormatter);
                        holder.barChart.setXAxisRenderer(xRenderer);
                        holder.barChart.setExtraBottomOffset(15);
                        YAxis yAxis = holder.barChart.getAxisLeft();
                        yAxis.setDrawLabels(false); // no axis labels
                        yAxis.setDrawAxisLine(false); // no axis line
                        yAxis.setDrawGridLines(false); // no grid lines
                        yAxis.setDrawZeroLine(true); // draw a zero line
                        holder.barChart.getAxisRight().setEnabled(false); // no right axis

                    }
                    //Button Handlers

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
                                            _expandedPosition = isExpanded ? -1 : position;
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
                            //TransitionManager.beginDelayedTransition(sessionRecyclerView);
                            _expandedPosition = isExpanded ? -1 : position;
                            notifyItemChanged(_previousExpandedPosition);
                            notifyItemChanged(position);
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
        public TextView sessionNameView, totalTimeView, totalTricksView;
        public View itemView;
        public ImageView removeButton;
        public BarChart barChart;
        public SessionViewHolder(View v) {
            super(v);
            itemView = v;
            sessionNameView = v.findViewById(R.id.sessionName);
            totalTimeView = v.findViewById(R.id.totalTimePracticed);
            totalTricksView = v.findViewById(R.id.totalTricks);
            removeButton = v.findViewById(R.id.removeButton);
            barChart = v.findViewById(R.id.sessionChart);
        }
    }

    public ArrayList<String> ConvertJSONtoList(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

}
