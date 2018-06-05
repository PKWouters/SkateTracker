package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    private List<String> challenges;
    private int _expandedPosition = -1, _previousExpandedPosition = -1;
    ViewGroup recyclerView;
    FirebaseFirestore db;
    FirebaseUser user;


    public ChallengeAdapter(List<String> challenges) {
        this.challenges = challenges;
    }


    //VIEW HOLDER STUFF
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView challengeNameView;
        public View itemView;
        public LinearLayout linearLayout;
        public ViewHolder(View v) {
            super(v);
            itemView = v;
            challengeNameView = v.findViewById(R.id.trickName);
            linearLayout = v.findViewById(R.id.linearlayout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_card_layout, parent, false);
        recyclerView = parent;
        ViewHolder vh = new ViewHolder(v);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(user != null){
            db.collection("challenges").document(challenges.get(position)).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            TransitionManager.beginDelayedTransition(recyclerView);
                            Map<String, Object> data = documentSnapshot.getData();
                            holder.challengeNameView.setText(data.get("name").toString());
                            holder.itemView.setVisibility(View.VISIBLE);
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            holder.itemView.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return challenges.size();
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



}
