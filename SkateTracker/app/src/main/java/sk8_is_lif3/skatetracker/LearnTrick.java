package sk8_is_lif3.skatetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;

import java.util.ArrayList;

public class LearnTrick extends Fragment {

    String mName, mUrl, mDBID, mArticle;
    ArrayList<String> mPrevTricks;
    YouTubePlayerView youTubePlayerView;

    public LearnTrick() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public LearnTrick(String name, String url, String dbID, String article, ArrayList<String> prevTricks) {
        mName = name;
        mUrl = url;
        mDBID = dbID;
        mArticle = article;
        mPrevTricks = prevTricks;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        TextView trickName = (TextView) getView().findViewById(R.id.trickName);
        trickName.setText(mName);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            DocumentReference docRef = db.collection("users").document(user.getUid()).collection("tricks").document(mDBID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            TextView progress = (TextView) getView().findViewById(R.id.progressNum);
                            Double ratio = (Double)document.getData().get("avgRatio") * 100;
                            progress.setText(ratio.intValue() + ".0%");
                        } else {
                            TextView progress = (TextView) getView().findViewById(R.id.progressNum);
                            progress.setText("0.0%");
                        }
                    } else {

                    }
                }
            });
        }

        TableLayout tbl = (TableLayout)getView().findViewById(R.id.prevTricksTable);
        youTubePlayerView = getView().findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        String videoId = mUrl;
                        initializedYouTubePlayer.loadVideo(videoId, 0);
                    }
                });
            }
        }, true);
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        if(mPrevTricks != null && mPrevTricks.size() > 0) {
            for(int i = 0; i < mPrevTricks.size(); i++) {
                final TableRow tblRow = new TableRow(getContext());
                tblRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                if(user != null) {
                    try {
                        DocumentReference docRef = db.collection("users").document(user.getUid()).collection("tricks").document(mPrevTricks.get(i));
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        if((Double)(document.getData().get("avgRatio")) >= 1.0){
                                            ImageView checkMark = new ImageView(getContext());
                                            checkMark.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                                            checkMark.setImageResource(R.drawable.fui_done_check_mark);
                                            tblRow.addView(checkMark);
                                        }
                                    } else {

                                    }
                                } else {

                                }
                            }
                        });
                    }catch (Exception e){

                    }

                }

                TextView roundNumView = new TextView(getContext());
                roundNumView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));

                if(mPrevTricks.get(i).length() > 6)
                    roundNumView.setText(mPrevTricks.get(i).substring(6).replaceAll("_", " ").toUpperCase());

                tblRow.addView(roundNumView);
                tbl.addView(tblRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }else{
            tbl.setVisibility(View.GONE);
        }
        TextView article = (TextView) getView().findViewById(R.id.article);
        article.setText(mArticle);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learn_trick, container, false);
    }

    @Override
    public void onPause(){
        super.onPause();
        youTubePlayerView.release();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
