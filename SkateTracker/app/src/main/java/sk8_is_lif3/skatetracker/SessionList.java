package sk8_is_lif3.skatetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadSystemException;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import sk8_is_lif3.skatetracker.database.AppDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SessionList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SessionList extends Fragment {

    private RecyclerView sessionRecyclerView;
    private RecyclerView.Adapter sessionAdapter;
    private RecyclerView.LayoutManager sessionLayoutManager;
    private AppDatabase database;
    List<Session> sessionList;

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
        sessionAdapter = new SessionAdapter(sessionList);
        //sessionRecyclerView.setAdapter(sessionAdapter);

        for (Session s:database.sessionDAO().getSessions()) {
            sessionList.add(s);
            System.out.println(s.tricksAddedString);
        }

        sessionRecyclerView.setAdapter(sessionAdapter);
        sessionAdapter.notifyDataSetChanged();

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
            sessionRecyclerView.setVisibility(View.GONE);
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
        sessionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = AppDatabase.getDatabase(getContext());
        sessionList = new ArrayList<Session>();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_session_list, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        sessionList.clear();
        if(database.sessionDAO().getSessions().size() > 0) {
            for (Session s:database.sessionDAO().getSessions()) {
                sessionList.add(s);
                sessionAdapter.notifyDataSetChanged();
            }
        }
    }

}
