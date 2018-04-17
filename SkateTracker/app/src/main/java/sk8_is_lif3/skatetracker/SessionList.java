package sk8_is_lif3.skatetracker;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadSystemException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private RecyclerView.Adapter sessionAdapter;
    private RecyclerView.LayoutManager sessionLayoutManager;
    private FirebaseUser user;
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

        FloatingActionButton floatingActionButton = (FloatingActionButton)getView().findViewById(R.id.newSessionFab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session curr = new Session();
                Intent intent = new Intent(getActivity(), CurrentSession.class);
                intent.putExtra("sessionID", curr.GetID());
                startActivity(intent);
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
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
