package sk8_is_lif3.fliptrick;

import android.annotation.SuppressLint;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeTransform;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sk8_is_lif3.fliptrick.transitions.Achievement;
import sk8_is_lif3.fliptrick.transitions.SessionNameTransition;


public class Profile extends Fragment {

    FirebaseFirestore db;
    FirebaseUser user;
    private TextView tv;
    private ImageView bg;
    private LinearLayoutManager trickLayoutManager;
    private RecyclerView trickGridView;
    private FirestoreRecyclerAdapter<TrickToDisplay, Profile.TrickViewHolder> trickAdapter;

    public Profile() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);

        trickLayoutManager = new LinearLayoutManager(getContext());
        trickGridView = getView().findViewById(R.id.mastedRecyclerView);
        trickGridView.setHasFixedSize(false);
        trickGridView.setLayoutManager(trickLayoutManager);
        trickGridView.setAdapter(trickAdapter);
        trickGridView.setNestedScrollingEnabled(true);
        final CardView cardView = getView().findViewById(R.id.cardView);

        setHasOptionsMenu(true);
        if(user != null) {
            tv.setText(user.getDisplayName());
            tv.setTextColor(getResources().getColor(R.color.textDark));
        }
        bg = (ImageView) getActivity().findViewById(R.id.stickerBombImage);
        if(bg != null) {
            bg.setAlpha(0.25f);
            bg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    TransitionManager.beginDelayedTransition((ViewGroup) view);
                    switch (action) {
                        case (MotionEvent.ACTION_DOWN):
                            bg.setAlpha(1f);
                            cardView.setAlpha(0.25f);
                            tv.setTextColor(getResources().getColor(R.color.textDarkTranslucent));
                            return true;
                        case (MotionEvent.ACTION_UP):
                            bg.setAlpha(0.25f);
                            cardView.setAlpha(1f);
                            tv.setTextColor(getResources().getColor(R.color.textDark));
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Query trickQuery = db.collection("users").document(user.getUid()).collection("tricks").whereGreaterThanOrEqualTo("avgRatio", 0.0).orderBy("avgRatio", Query.Direction.DESCENDING).limit(4);
            FirestoreRecyclerOptions<TrickToDisplay> trickOptions = new FirestoreRecyclerOptions.Builder<TrickToDisplay>()
                    .setQuery(trickQuery, TrickToDisplay.class)
                    .build();
            trickAdapter = new FirestoreRecyclerAdapter<TrickToDisplay, Profile.TrickViewHolder>(trickOptions) {
                @NonNull
                @Override
                public Profile.TrickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    // create a new view
                    View v = (View) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.session_card_layout, parent, false);
                    final Profile.TrickViewHolder vh = new Profile.TrickViewHolder(v);

                    return vh;
                }

                @Override
                protected void onBindViewHolder(@NonNull final Profile.TrickViewHolder holder, final int position, @NonNull final TrickToDisplay model) {
                    //final CardView cardView = holder.itemView.findViewById(R.id.card_view);
                    holder.trickNameView.setText(model.getName());

                }
            };

            trickAdapter.startListening();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tv = (TextView)v.findViewById(R.id.welcomename);
        return v;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(user != null && bg != null) {
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> data = documentSnapshot.getData();
                    try{
                        String image = data.get("stickerBombUrl").toString();
                        Picasso.get().load(image).into(bg);
                    }catch (Exception e){

                    }
                }
            });
        }
        if(trickAdapter != null){
            trickAdapter.startListening();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(trickAdapter != null)
            trickAdapter.stopListening();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.the_achivements:

                        Intent intent = new Intent(getContext(), AchievementActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        getActivity().startActivity(intent);
                        break;
            case R.id.sticker_bomb:
                Intent stickerintent = new Intent(getContext(), StickerBombPage.class);
                stickerintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().startActivity(stickerintent);
                break;
            case R.id.the_sign_out:
                        if(FirebaseAuth.getInstance().getCurrentUser() != null){
                            AuthUI.getInstance()
                                    .signOut(getActivity())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(new Intent(getContext(), LoginActivity.class));
                                            Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        }
                                    });
                        }
                        break;
        }
        return true;
    }

    private class TrickViewHolder extends RecyclerView.ViewHolder {
        private View view;

        // each data item is just a string in this case
        public TextView trickNameView;
        public View itemView;
        public TrickViewHolder(View v) {
            super(v);
            itemView = v;
            trickNameView = v.findViewById(R.id.trickName);
        }
    }
}
