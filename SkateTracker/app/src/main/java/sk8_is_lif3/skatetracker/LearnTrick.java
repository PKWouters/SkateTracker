package sk8_is_lif3.skatetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
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

        if(mPrevTricks.size() > 0) {
            for(int i = 0; i < mPrevTricks.size(); i++) {
                TableRow tblRow = new TableRow(getContext());
                tblRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView roundNumView = new TextView(getContext());
                roundNumView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));

                if(mPrevTricks.get(i).length() > 6)
                    roundNumView.setText(mPrevTricks.get(i).substring(6).replaceAll("_", " ").toUpperCase());

                tblRow.addView(roundNumView);
                tbl.addView(tblRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
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
