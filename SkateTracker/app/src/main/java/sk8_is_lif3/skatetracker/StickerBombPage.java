package sk8_is_lif3.skatetracker;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class StickerBombPage extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseUser user;

    int windowwidth; // Actually the width of the RelativeLayout.
    int windowheight; // Actually the height of the RelativeLayout.

    private int xDelta;
    private int yDelta;

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_bomb_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        final RelativeLayout layout = findViewById(R.id.layout);

        windowheight = layout.getHeight();
        windowwidth = layout.getWidth();

        if(user != null){

            db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        Map<String, Object> userData = documentSnapshot.getData();
                        List<String> achievements = (List<String>) userData.get("achievements");

                        for(String achievement : achievements){
                            db.collection("achievements").document(achievement).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()) {
                                        Map<String, Object> data = documentSnapshot.getData();
                                        final ImageView sticker = new ImageView(getApplicationContext());
                                        RelativeLayout.LayoutParams vp =
                                                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                                        sticker.setLayoutParams(vp);
                                        sticker.setOnTouchListener(new View.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View view, MotionEvent event) {
                                                {

                                                    int x = (int) event.getRawX();
                                                    int y = (int) event.getRawY();


                                                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

                                                    switch (event.getAction() & MotionEvent.ACTION_MASK) {

                                                        case MotionEvent.ACTION_DOWN:
                                                            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                                                    view.getLayoutParams();

                                                            xDelta = x - lParams.leftMargin;
                                                            yDelta = y - lParams.topMargin;
                                                            break;

                                                        case MotionEvent.ACTION_UP:
                                                            layoutParams = (RelativeLayout.LayoutParams) view
                                                                    .getLayoutParams();

                                                            int x_cord = x - xDelta;
                                                            int y_cord = y - yDelta;
                                                            layoutParams.leftMargin = x_cord;
                                                            layoutParams.topMargin = y_cord;
                                                            layoutParams.bottomMargin = windowheight - (y_cord - sticker.getHeight());
                                                            layoutParams.rightMargin = windowwidth - (x_cord - sticker.getWidth());
                                                            view.setLayoutParams(layoutParams);
                                                            break;

                                                        case MotionEvent.ACTION_MOVE:
                                                            layoutParams = (RelativeLayout.LayoutParams) view
                                                                .getLayoutParams();
                                                            x_cord = x - xDelta;
                                                            y_cord = y - yDelta;
                                                            layoutParams.leftMargin = x_cord;
                                                            layoutParams.topMargin = y_cord;
                                                            layoutParams.bottomMargin = windowheight - (y_cord - sticker.getHeight());
                                                            layoutParams.rightMargin = windowwidth - (x_cord - sticker.getWidth());
                                                            view.setLayoutParams(layoutParams);
                                                            break;
                                                    }
                                                    layout.invalidate();
                                                    return true;
                                                }
                                            }
                                        });
                                        Picasso.get().load(data.get("stickerUrl").toString()).into(sticker);
                                        layout.addView(sticker);
                                    }
                                }
                            });
                        }

                    }
                }
            });
        }
    }
}
