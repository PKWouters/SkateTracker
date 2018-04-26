package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeTransform;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.transition.Transition;
import android.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                    //final CardView cardView = holder.itemView.findViewById(R.id.card_view);
                    holder.sessionNameView.setText(model.getDate() + " - " + model.getName());
                    holder.sessionNameView.setMaxLines(2);
                    holder.sessionNameView.setTransitionName("sessionNameTransition" + model.getId());
                    holder.sessionNameView.setTextColor(Color.WHITE);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Transition mainTransition = TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade);
                            mainTransition.setDuration(250);
                            mainTransition.setStartDelay(375);

                            Transition textTransScale = new SessionNameTransition();
                            textTransScale.setDuration(375);
                            textTransScale.setInterpolator(new FastOutSlowInInterpolator());

                            Transition textTransMove = new ChangeTransform();
                            textTransMove.setDuration(375);
                            textTransMove.setInterpolator(new FastOutSlowInInterpolator());

                            Transition textTransBounds = new ChangeBounds();
                            textTransBounds.setDuration(375);
                            textTransBounds.setInterpolator(new FastOutSlowInInterpolator());

                            long duration = 375;

                            int screenSize = getView().getResources().getConfiguration().screenLayout &
                                    Configuration.SCREENLAYOUT_SIZE_MASK;

                            switch (screenSize) {
                                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                                    duration = 390;
                                    break;
                                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                                    duration = 300;
                                    break;
                                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                                    duration = 210;
                                    break;
                                default:
                            }
                            mainTransition.setStartDelay(duration);
                            textTransScale.setDuration(duration);
                            textTransMove.setDuration(duration);
                            textTransBounds.setDuration(duration);

                            TransitionSet tSet = new TransitionSet().addTransition(textTransMove).addTransition(textTransScale).addTransition(textTransBounds);

                            setSharedElementReturnTransition(tSet);
                            //setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                            setEnterTransition(null);
                            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                            setReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_left));
                            setReenterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_left));

                            SessionDetailFragment nextFrag = new SessionDetailFragment(model.getDate() + " - " + model.getName(), model.getTotalTimeFormatted(), model.getId(), model.getTricks());

                            nextFrag.setSharedElementEnterTransition(tSet);
                            nextFrag.setEnterTransition(mainTransition);
                            nextFrag.setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_right));
                            nextFrag.setReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_right));

                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setReorderingAllowed(true)
                                    .addSharedElement(holder.sessionNameView, holder.sessionNameView.getTransitionName())
                                    .replace(R.id.fragment, nextFrag,"SessionDetailFragment")
                                    .addToBackStack(model.getId())
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
        }
    }

    public ArrayList<String> ConvertJSONtoList(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

}
