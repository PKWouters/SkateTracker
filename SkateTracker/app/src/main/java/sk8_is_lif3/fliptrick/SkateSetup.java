package sk8_is_lif3.fliptrick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SkateSetup extends AppCompatActivity {

    int gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skate_setup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Button next = (Button) findViewById(R.id.StartSkateB);
        final EditText pone = (EditText) findViewById(R.id.PlayerOneT);
        final EditText ptwo = (EditText) findViewById(R.id.PlayerTwoT);

        gameMode = getIntent().getIntExtra("gamemode", 0);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String player1;
                String player2;
                player1 = pone.getText().toString();
                player2 = ptwo.getText().toString();
                if(player1.isEmpty() || player2.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Both Names must be entered", Toast.LENGTH_SHORT).show();
                }else{
                    Intent myIntent = new Intent(view.getContext(), SkateGame.class);
                    Bundle extras = new Bundle();
                    extras.putString("player1Name", player1);
                    extras.putString("player2Name", player2);
                    extras.putInt("gamemode", gameMode);
                    myIntent.putExtras(extras);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivityForResult(myIntent, 0);
                }
            }
        });

    }
}
