package sk8_is_lif3.skatetracker;

import android.app.DownloadManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import sk8_is_lif3.skatetracker.R;
import sk8_is_lif3.skatetracker.SessionToDisplay;
import sk8_is_lif3.skatetracker.TrickDetailFragment;
import sk8_is_lif3.skatetracker.TrickToDisplay;
import sk8_is_lif3.skatetracker.transitions.Challenge;

public class AchievementActivity extends AppCompatActivity {

    private FirebaseUser user;
    RecyclerView challengesRecyclerView;
    LinearLayoutManager challengeLayoutManager;
    ChallengeAdapter tempAdapter;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {

            db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> data = documentSnapshot.getData();
                    List<String> userChallenges = (List<String>)data.get("challenges");
                    if(userChallenges != null && userChallenges.size() > 0){
                        // Inflate the layout for this fragment
                        challengesRecyclerView = findViewById(R.id.challengesRecyclerView);
                        challengeLayoutManager = new LinearLayoutManager(getApplicationContext());
                        challengesRecyclerView.setLayoutManager(challengeLayoutManager);

                        tempAdapter = new ChallengeAdapter(userChallenges);
                        adapter = (RecyclerView.Adapter) tempAdapter;
                        challengesRecyclerView.setAdapter(adapter);
                        challengesRecyclerView.setNestedScrollingEnabled(false);
                        adapter.notifyDataSetChanged();
                    }
                }
            });

        }

    }
}

