package sk8_is_lif3.skatetracker;

import android.arch.persistence.room.Update;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
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


public class SkateGame extends AppCompatActivity {

    ImageButton landBtn, failBtn;
    int offPlayer, currentPlayer;
    int playerOneScore = 0, playerTwoScore = 0;
    int roundNum = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skate_game);

        //---INTENT STUFF HERE---//



        //-----------------------//

        final int gameMode = 0;
        offPlayer = 1;
        currentPlayer = 1;

        landBtn = (ImageButton)findViewById(R.id.landedButton);
        failBtn = (ImageButton)findViewById(R.id.failedButton);

        UpdateCards(gameMode);

        if(gameMode == 0) { //---CLASSIC SKATE---//
            landBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playerOneScore < 5 && playerTwoScore < 5) {
                        if (currentPlayer == 1) {
                            currentPlayer = 2;
                            if (offPlayer == 2)
                                roundNum++;
                        } else if (currentPlayer == 2) {
                            currentPlayer = 1;
                            if (offPlayer == 1)
                                roundNum++;
                        }
                        UpdateCards(gameMode);
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
                                roundNum++;
                                TextView letterToChange;
                                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                                switch (playerTwoScore) {
                                    case (1):
                                        letterToChange = findViewById(R.id.s_TextP2);
                                        letterToChange.setTextSize(70);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (2):
                                        letterToChange = findViewById(R.id.k_TextP2);
                                        letterToChange.setTextSize(70);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (3):
                                        letterToChange = findViewById(R.id.a_TextP2);
                                        letterToChange.setTextSize(70);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (4):
                                        letterToChange = findViewById(R.id.t_TextP2);
                                        letterToChange.setTextSize(70);
                                        letterToChange.setTextColor(Color.WHITE);
                                        letterToChange.setTypeface(null, Typeface.BOLD);
                                        break;
                                    case (5):
                                        letterToChange = findViewById(R.id.e_TextP2);
                                        letterToChange.setTextSize(70);
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
                        UpdateCards(gameMode);
                    }
                }
            });
        }else if(gameMode == 1){ //---PRE-GEN SKATE---//

        }

    }
    private void UpdateCards(int gameMode){

        if(gameMode == 0) {
            if(currentPlayer == 1){
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                CardView p1Card = findViewById(R.id.p1Card);
                p1Card.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                if(offPlayer == 2) {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                    CardView p2Card = findViewById(R.id.p2Card);
                    p2Card.setCardBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                }else {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                    CardView p2Card = findViewById(R.id.p2Card);
                    p2Card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }

            }else if(currentPlayer == 2){
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p2Card));
                CardView p2Card = findViewById(R.id.p2Card);
                p2Card.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));

                if(offPlayer == 1) {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                    CardView p1Card = findViewById(R.id.p1Card);
                    p1Card.setCardBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                }else {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.p1Card));
                    CardView p1Card = findViewById(R.id.p1Card);
                    p1Card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
                TextView roundNumView = findViewById(R.id.roundTrickText);
                roundNumView.setText("Round: " + roundNum);
        }
    }
}
