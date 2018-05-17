package sk8_is_lif3.skatetracker;

import android.content.Context;
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
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import sk8_is_lif3.skatetracker.transitions.SessionNameTransition;

public class LearnHome extends Fragment {
    private FirestoreRecyclerAdapter<TrickToLearn, TrickViewHolder> easyTrickAdapter;
    private RecyclerView trickGridView;
    private LinearLayoutManager trickLayoutManager;
    private String recentTrick;
    private TrickToDisplay recentTrickObj;

    public LearnHome() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LearnHome newInstance(String param1, String param2) {
        LearnHome fragment = new LearnHome();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        trickLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trickGridView = getView().findViewById(R.id.easyTrickRecyclerView);
        trickGridView.setHasFixedSize(false);
        trickGridView.setLayoutManager(trickLayoutManager);
        trickGridView.setAdapter(easyTrickAdapter);
        final CardView recentTrickCard = (CardView) getView().findViewById(R.id.recentCard);
        recentTrickCard.setVisibility(View.GONE);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.getData().get("recent_trick") != null) {
                                recentTrick = document.getData().get("recent_trick").toString();
                                DocumentReference trickRef = db.collection("users").document(user.getUid()).collection("tricks").document(recentTrick);
                                trickRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        recentTrickObj = documentSnapshot.toObject(TrickToDisplay.class);
                                        if(getView() != null) {
                                            final DonutProgress progress = (DonutProgress) getView().findViewById(R.id.trickProgress);
                                            Double ratio = (Double) recentTrickObj.getAvgRatio();
                                            ratio *= 100;
                                            TextView recentTrickView = (TextView) getView().findViewById(R.id.recentTrickName);
                                            recentTrickView.setText(recentTrickObj.getName());
                                            if(ratio > 100){
                                                ratio = 100.0;
                                            }
                                            else if (ratio < 100) {
                                                progress.setTextColor(getResources().getColor(R.color.colorAccent));
                                                progress.setFinishedStrokeColor(getResources().getColor(R.color.colorAccent));
                                            }
                                            progress.setDonut_progress(Integer.toString(ratio.intValue()));
                                            recentTrickCard.setVisibility(View.VISIBLE);
                                            ProgressBar loading = (ProgressBar) getView().findViewById(R.id.progressBar);
                                            loading.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        } else {

                        }
                    } else {

                    }
                }
            });
        }



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        setReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        setReenterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query easyTrickQuery = db.collection("tricks").whereEqualTo("difficulty", "easy").limit(10);

        FirestoreRecyclerOptions<TrickToLearn> trickOptions = new FirestoreRecyclerOptions.Builder<TrickToLearn>()
                .setQuery(easyTrickQuery, TrickToLearn.class)
                .build();

        easyTrickAdapter = new FirestoreRecyclerAdapter<TrickToLearn, TrickViewHolder>(trickOptions) {
            @NonNull
            @Override
            public TrickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // create a new view
                View v = (View) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trick_learn_card_layout, parent, false);
                final TrickViewHolder vh = new TrickViewHolder(v);

                return vh;
            }

            @Override
            protected void onBindViewHolder(@NonNull final TrickViewHolder holder, final int position, @NonNull final TrickToLearn model) {
                //final CardView cardView = holder.itemView.findViewById(R.id.card_view);
                holder.trickNameView.setText(model.getName());
                holder.trickNameView.setMaxLines(1);
                holder.trickNameView.setTextColor(Color.WHITE);
                final String videoID = model.getUrl().split("v=")[1];
                String thumbnail = "http://img.youtube.com/vi/" + videoID + "/mqdefault.jpg";
                Picasso.get().load(thumbnail).into(holder.background);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LearnTrick nextFrag = new LearnTrick(model.getName(), videoID, model.getId(), model.getArticle(), model.getPrevTricks());

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragment, nextFrag,"LearnTrick")
                                .addToBackStack(model.getName())
                                .commit();
                    }
                });
            }
        };
        easyTrickAdapter.startListening();

    }

    private class TrickViewHolder extends RecyclerView.ViewHolder {
        private View view;

        // each data item is just a string in this case
        public TextView trickNameView;
        public View itemView;
        public ImageView background;
        public TrickViewHolder(View v) {
            super(v);
            itemView = v;
            trickNameView = v.findViewById(R.id.trickName);
            background = v.findViewById(R.id.trickBackground);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_learn_home, container, false);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    @Override
    public void onResume() {
        super.onResume();
        if(easyTrickAdapter != null){
            easyTrickAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(easyTrickAdapter != null){
            easyTrickAdapter.stopListening();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}