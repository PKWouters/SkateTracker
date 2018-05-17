package sk8_is_lif3.skatetracker;

import android.arch.persistence.room.Update;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class SkateGame extends AppCompatActivity {

    ImageButton landBtn, failBtn;
    int offPlayer, currentPlayer;
    int playerOneScore = 0, playerTwoScore = 0;
    int roundNum = 1;
    String p1, p2, currTrick = "";
    Map<String, String> resultsMap;
    ArrayList<String> trickList;
    boolean p1Landed = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skate_game);

        //---INTENT STUFF HERE---//
        Intent recieveIntent = getIntent();
        p1 = recieveIntent.getExtras().getString("player1Name");
        p2 = recieveIntent.getExtras().getString("player2Name");

        TextView p1NameView = (TextView)findViewById(R.id.player1NameView);
        TextView p2NameView = (TextView)findViewById(R.id.player2NameView);
        p1NameView.setText(p1);
        p2NameView.setText(p2);

        resultsMap = new HashMap<String, String>();

        //-----------------------//

        final int gameMode = recieveIntent.getExtras().getInt("gamemode", 0);
        offPlayer = 1;
        currentPlayer = 1;

        landBtn = (ImageButton)findViewById(R.id.landedButton);
        failBtn = (ImageButton)findViewById(R.id.failedButton);
        UpdateCards(gameMode, currTrick);

        if(gameMode == 0) { //---CLASSIC SKATE---//
            landBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playerOneScore < 5 && playerTwoScore < 5) {
                        if (currentPlayer == 1) {
                            currentPlayer = 2;
                            if (offPlayer == 2) {
                                resultsMap.put("Round: " + roundNum, "both");
                                roundNum++;
                            }
                        }else if (currentPlayer == 2) {
                            currentPlayer = 1;
                            if (offPlayer == 1) {
                                resultsMap.put("Round: " + roundNum, "both");
                                roundNum++;
                            }
                        }
                        UpdateCards(gameMode, currTrick);
                    }
                }
            });
            failBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playerOneScore < 5 && playerTwoScore < 5) {
                        if (offPlayer == 1) {
                            if (currentPlayer == 1) {
                                currentPlayer = 2;
                                offPlayer = 2;
                            } else if (currentPlayer == 2) {
                                playerTwoScore++;
                                resultsMap.put("Round: " + roundNum, "p1");
                                roundNum++;
                                TextView letterToChange;
                                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                                switch (playerTwoScore) {
                                    case (1):
                                        letterToChange = findViewById(R.id.s_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (2):
                                        letterToChange = findViewById(R.id.k_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (3):
                                        letterToChange = findViewById(R.id.a_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (4):
                                        letterToChange = findViewById(R.id.t_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (5):
                                        letterToChange = findViewById(R.id.e_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                }

                                currentPlayer = 1;
                            }
                        } else if (offPlayer == 2) {
                            if (currentPlayer == 1) {
                                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                                playerOneScore++;
                                resultsMap.put("Round: " + roundNum, "p2");
                                roundNum++;
                                TextView letterToChange;
                                switch (playerOneScore) {
                                    case (1):
                                        letterToChange = findViewById(R.id.s_Text);
                                        letterToChange.setTextSize(70);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (2):
                                        letterToChange = findViewById(R.id.k_Text);
                                        letterToChange.setTextSize(70);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (3):
                                        letterToChange = findViewById(R.id.a_Text);
                                        letterToChange.setTextSize(70);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (4):
                                        letterToChange = findViewById(R.id.t_Text);
                                        letterToChange.setTextSize(70);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (5):
                                        letterToChange = findViewById(R.id.e_Text);
                                        letterToChange.setTextSize(70);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                }
                                currentPlayer = 2;
                            } else if (currentPlayer == 2) {
                                currentPlayer = 1;
                                offPlayer = 1;
                            }
                        }
                        UpdateCards(gameMode, currTrick);
                    }
                }
            });
        }else if(gameMode == 1){ //---PRE-GEN SKATE---//


            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            DocumentReference docRef = db.collection("tricks").document("trick_LIST");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            trickList = (ArrayList<String>)(data.get("list"));
                            currTrick = GetNewTrick();
                            TextView roundNumView = findViewById(R.id.roundTrickText);
                            roundNumView.setText(currTrick);
                        } else {
                            Toast.makeText(getApplicationContext(), "Could Not Load Trick List", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                }
            });

            landBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playerOneScore < 5 && playerTwoScore < 5) {
                        if (currentPlayer == 1) {
                            currentPlayer = 2;
                            p1Landed = true;

                        }else if (currentPlayer == 2) {
                            currentPlayer = 1;
                            roundNum++;
                            if(p1Landed)
                                resultsMap.put(roundNum + "_" + currTrick, "both");
                            else
                                resultsMap.put(roundNum + "_" + currTrick, "p2");
                            currTrick = GetNewTrick();
                        }
                        UpdateCards(gameMode, currTrick);
                    }
                }
            });
            failBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playerOneScore < 5 && playerTwoScore < 5) {
                        if (currentPlayer == 2) {
                                playerTwoScore++;
                                roundNum++;
                                if(p1Landed)
                                    resultsMap.put(roundNum + "_" + currTrick, "p1");
                                else
                                    resultsMap.put(roundNum + "_" + currTrick, "none");
                                currTrick = GetNewTrick();
                                TextView letterToChange;
                                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                                switch (playerTwoScore) {
                                    case (1):
                                        letterToChange = findViewById(R.id.s_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (2):
                                        letterToChange = findViewById(R.id.k_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (3):
                                        letterToChange = findViewById(R.id.a_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (4):
                                        letterToChange = findViewById(R.id.t_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (5):
                                        letterToChange = findViewById(R.id.e_TextP2);
                                        letterToChange.setTextSize(80);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                }

                                currentPlayer = 1;
                            }
                        else if (currentPlayer == 1) {
                            TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                            playerOneScore++;
                            p1Landed = false;
                            TextView letterToChange;
                            switch (playerOneScore) {
                                case (1):
                                    letterToChange = findViewById(R.id.s_Text);
                                    letterToChange.setTextSize(80);
                                    letterToChange.setTextColor(Color.WHITE);
                                    letterToChange.setTypeface(null, Typeface.BOLD);
                                    break;
                                case (2):
                                    letterToChange = findViewById(R.id.k_Text);
                                    letterToChange.setTextSize(80);
                                    letterToChange.setTextColor(Color.WHITE);
                                    letterToChange.setTypeface(null, Typeface.BOLD);
                                    break;
                                case (3):
                                    letterToChange = findViewById(R.id.a_Text);
                                    letterToChange.setTextSize(80);
                                    letterToChange.setTextColor(Color.WHITE);
                                    letterToChange.setTypeface(null, Typeface.BOLD);
                                    break;
                                case (4):
                                    letterToChange = findViewById(R.id.t_Text);
                                    letterToChange.setTextSize(80);
                                    letterToChange.setTextColor(Color.WHITE);
                                    letterToChange.setTypeface(null, Typeface.BOLD);
                                    break;
                                case (5):
                                    letterToChange = findViewById(R.id.e_Text);
                                    letterToChange.setTextSize(80);
                                    letterToChange.setTextColor(Color.WHITE);
                                    letterToChange.setTypeface(null, Typeface.BOLD);
                                    break;
                            }
                            currentPlayer = 2;
                        }
                        UpdateCards(gameMode, currTrick);
                    }
                }
            });

        }

    }

    private String GetNewTrick(){
        Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((trickList.size() - 0)) + 0;
        return trickList.get(randomNum).substring(6).replaceAll("_"," ").toUpperCase();
    }

    private void UpdateCards(int gameMode, String currTrick){

        if(gameMode == 0) {
            if(currentPlayer == 1){
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                CardView p1Card = findViewById(R.id.p1Card);
                p1Card.setCardBackgroundColor(getResources().getColor(R.color.skatecolorAccent));
                if(offPlayer == 2) {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                    CardView p2Card = findViewById(R.id.p2Card);
                    p2Card.setCardBackgroundColor(getResources().getColor(R.color.skatesetcolorAccent));
                }else {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                    CardView p2Card = findViewById(R.id.p2Card);
                    p2Card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }

            }else if(currentPlayer == 2){
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                CardView p2Card = findViewById(R.id.p2Card);
                p2Card.setCardBackgroundColor(getResources().getColor(R.color.skatecolorAccent));

                if(offPlayer == 1) {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                    CardView p1Card = findViewById(R.id.p1Card);
                    p1Card.setCardBackgroundColor(getResources().getColor(R.color.skatesetcolorAccent));
                }else {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                    CardView p1Card = findViewById(R.id.p1Card);
                    p1Card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
                TextView roundNumView = findViewById(R.id.roundTrickText);
                roundNumView.setText("Round: " + roundNum);
        }else{

            if(currentPlayer == 1){
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                CardView p1Card = findViewById(R.id.p1Card);
                p1Card.setCardBackgroundColor(getResources().getColor(R.color.skatecolorAccent));
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                CardView p2Card = findViewById(R.id.p2Card);
                p2Card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            }else if(currentPlayer == 2){
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                CardView p2Card = findViewById(R.id.p2Card);
                p2Card.setCardBackgroundColor(getResources().getColor(R.color.skatecolorAccent));
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                CardView p1Card = findViewById(R.id.p1Card);
                p1Card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            TextView roundNumView = findViewById(R.id.roundTrickText);
            roundNumView.setText(currTrick);

        }
        //END SCREEN DIALOG
        if(playerOneScore >= 5 || playerTwoScore >= 5){
            ResultsFragment resultsDialog = new ResultsFragment(resultsMap, gameMode, p2.toString(), p1.toString(), p2.toString());
            if(playerOneScore < playerTwoScore){
                resultsDialog = new ResultsFragment(resultsMap, gameMode, p1.toString(), p1.toString(), p2.toString());
            }
            resultsDialog.show(getSupportFragmentManager(), "resultsDialog");
            resultsDialog.setCancelable(false);
        }
    }
}
