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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    private List<Map<String, Object>> challenges;
    private List<String> userChallenges;
    private int _expandedPosition = -1, _previousExpandedPosition = -1;
    ViewGroup recyclerView;
    FirebaseFirestore db;
    FirebaseUser user;


    public ChallengeAdapter(final List<Map<String, Object>> challenges, final List<String> userChallenges) {
        this.challenges = challenges;
        this.userChallenges = userChallenges;
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    //VIEW HOLDER STUFF
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView challengeNameView, challengeDescriptionView;
        public ImageView hasLandedView;
        public View itemView;
        public LinearLayout linearLayout;
        public ViewHolder(View v) {
            super(v);
            itemView = v;
            challengeNameView = v.findViewById(R.id.trickName);
            challengeDescriptionView = v.findViewById(R.id.description);
            hasLandedView = v.findViewById(R.id.hasLanded);
            linearLayout = v.findViewById(R.id.linearlayout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achievment_card_layout, parent, false);
        recyclerView = parent;
        ViewHolder vh = new ViewHolder(v);

        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(user != null){
            final boolean isExpanded = position == _expandedPosition;
            boolean hasEarned = false;
            if(userChallenges != null) {
                for (int i = 0; i < userChallenges.size(); i++) {
                    if (userChallenges.get(i).equals(challenges.get(position).get("id").toString())) {
                        hasEarned = true;
                    }
                }
            }
            if(isExpanded)
                _previousExpandedPosition = position;
            String id = challenges.get(position).get("id").toString();
            holder.challengeNameView.setText(challenges.get(position).get("name").toString());
            holder.challengeDescriptionView.setText(challenges.get(position).get("description").toString());
            holder.challengeDescriptionView.setVisibility(isExpanded?View.VISIBLE:View.GONE);
            holder.hasLandedView.setVisibility(hasEarned?View.VISIBLE:View.GONE);
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _expandedPosition = isExpanded ? -1:position;
                    notifyItemChanged(_previousExpandedPosition);
                    notifyItemChanged(position);
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
