package sk8_is_lif3.skatetracker;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Matrix;
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

    private Matrix mImageMatrix;
    /* Last Rotation Angle */
    private int mLastAngle = 0;
    /* Pivot Point for Transforms */
    private int mPivotX, mPivotY;

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

        mImageMatrix = new Matrix();

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
                                        sticker.setScaleType(ImageView.ScaleType.MATRIX);
                                        sticker.setOnTouchListener(new View.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                {

                                                    int x = (int) event.getRawX();
                                                    int y = (int) event.getRawY();

                                                    ImageView view = (ImageView) v;

                                                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

                                                    switch (event.getAction() & MotionEvent.ACTION_MASK) {

                                                        case MotionEvent.ACTION_DOWN:
                                                            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                                                    view.getLayoutParams();

                                                            xDelta = x - lParams.leftMargin;
                                                            yDelta = y - lParams.topMargin;
                                                            break;

                                                        case MotionEvent.ACTION_UP:
                                                            break;

                                                        case MotionEvent.ACTION_MOVE:
                                                            switch (event.getPointerCount()) {
                                                                case 2:
                                                                    // With two fingers down, rotate the image
                                                                    // following the fingers
                                                                    return doRotationEvent(view, event);
                                                                default:
                                                                    layoutParams = (RelativeLayout.LayoutParams) view
                                                                            .getLayoutParams();

                                                                    int x_cord = x - xDelta;
                                                                    int y_cord = y - yDelta;
                                                                    layoutParams.leftMargin = x_cord;
                                                                    layoutParams.topMargin = y_cord;
                                                                    layoutParams.bottomMargin = windowheight - (y_cord - sticker.getHeight());
                                                                    layoutParams.rightMargin = windowwidth - (x_cord - sticker.getWidth());
                                                                    view.setLayoutParams(layoutParams);
                                                            }
                                                            break;
                                                    }


                                                }
                                                return true;
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
    private boolean doRotationEvent(ImageView view, MotionEvent event) {
        //Calculate the angle between the two fingers
        float deltaX = event.getX(0) - event.getX(1);
        float deltaY = event.getY(0) - event.getY(1);
        double radians = Math.atan(deltaY / deltaX);
        //Convert to degrees
        int degrees = (int)(radians * 180 / Math.PI);

        float midX = event.getX(0) + event.getX(1);
        float midY = event.getY(0) + event.getY(1);
        /*
         * Must use getActionMasked() for switching to pick up pointer events.
         * These events have the pointer index encoded in them so the return
         * from getAction() won't match the exact action constant.
         */
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                //Mark the initial angle
                mLastAngle = degrees;
                break;
            case MotionEvent.ACTION_MOVE:
                // ATAN returns a converted value between -90deg and +90deg
                // which creates a point when two fingers are vertical where the
                // angle flips sign.  We handle this case by rotating a small amount
                // (5 degrees) in the direction we were traveling
                if ((degrees - mLastAngle) > 45) {
                    //Going CCW across the boundary
                    mImageMatrix.postRotate(-5, view.getWidth()/2, view.getHeight()/2);
                } else if ((degrees - mLastAngle) < -45) {
                    //Going CW across the boundary
                    mImageMatrix.postRotate(5, view.getWidth()/2, view.getHeight()/2);
                } else {
                    //Normal rotation, rotate the difference
                    mImageMatrix.postRotate(degrees - mLastAngle, view.getWidth()/2, view.getHeight()/2);
                }
                //Post the rotation to the image
                view.setImageMatrix(mImageMatrix);
                //Save the current angle
                mLastAngle = degrees;

                break;
        }

        return true;
    }

}
