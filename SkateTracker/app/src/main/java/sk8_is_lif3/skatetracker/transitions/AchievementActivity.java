package sk8_is_lif3.skatetracker.transitions;

import android.app.DownloadManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import sk8_is_lif3.skatetracker.R;
import sk8_is_lif3.skatetracker.SessionToDisplay;
import sk8_is_lif3.skatetracker.TrickDetailFragment;
import sk8_is_lif3.skatetracker.TrickToDisplay;

public class AchievementActivity extends AppCompatActivity {

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            Query query = db.collection("challenges").document().collection("sessions").orderBy("date", Query.Direction.DESCENDING).limit(4);
            //Query trickQuery = db.collection("users").document(user.getUid()).collection("tricks").whereGreaterThanOrEqualTo("avgRatio", 0.0).orderBy("avgRatio", Query.Direction.DESCENDING).limit(4);


            FirestoreRecyclerOptions<SessionToDisplay> options = new FirestoreRecyclerOptions.Builder<SessionToDisplay>()
                    .setQuery(query, Challenge.class)
                    .build();

            FirestoreRecyclerOptions<TrickToDisplay> trickOptions = new FirestoreRecyclerOptions.Builder<TrickToDisplay>()
                    .setQuery(trickQuery, TrickToDisplay.class)
                    .build();

            trickAdapter = new FirestoreRecyclerAdapter<TrickToDisplay, TrickViewHolder>(trickOptions) {
                @NonNull
                @Override
                public TrickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    // create a new view
                    View v = (View) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.trick_grid_card_layout, parent, false);
                    final TrickViewHolder vh = new TrickViewHolder(v);

                    return vh;
                }

                @Override
                protected void onBindViewHolder(@NonNull final TrickViewHolder holder, final int position, @NonNull final TrickToDisplay model) {
                    //final CardView cardView = holder.itemView.findViewById(R.id.card_view);
                    holder.trickNameView.setText(model.getName());
                    holder.trickNameView.setMaxLines(1);
                    holder.trickNameView.setTransitionName("trickNameTransition" + Integer.toString(position));
                    holder.trickNameView.setTextColor(Color.WHITE);
                    DecimalFormat df = new DecimalFormat("#.##");
                    df.setRoundingMode(RoundingMode.CEILING);
                    double val = Double.valueOf(df.format(model.getAvgRatio()));
                    holder.trickRatioView.setText(Double.toString(val * 100) + "%");
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

                            TrickDetailFragment nextFrag = new TrickDetailFragment(model.getName().toUpperCase().toString(), model.getAvgRatio(), model.getDbID(), model.getSessions());

                            nextFrag.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                            nextFrag.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                            nextFrag.setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                            nextFrag.setReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setReorderingAllowed(true)
                                    .addSharedElement(holder.trickNameView, holder.trickNameView.getTransitionName())
                                    .replace(R.id.fragment, nextFrag, "TrickDetailFragment")
                                    .addToBackStack(model.getName())
                                    .commit();
                        }
                    });
                }
            };
            trickAdapter.startListening();
        }
    }
}
