package sk8_is_lif3.skatetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class SkateGame extends AppCompatActivity {

    ImageButton landBtn, failBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skate_game);

        landBtn = (ImageButton)findViewById(R.id.landedButton);
        failBtn = (ImageButton)findViewById(R.id.failedButton);

        landBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        failBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
