package sk8_is_lif3.fliptrick;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
    ProgressDialog progressDialog;

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
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorBackground));

        final Random rnd = new Random();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        final RelativeLayout layout = findViewById(R.id.layout);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int windowwidth = displaymetrics.widthPixels;
        final int windowheight = displaymetrics.heightPixels-32;

        mImageMatrix = new Matrix();

        if(user != null && layout != null){

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
                                        int randomX = rnd.nextInt(windowwidth)/2;
                                        int randomY = rnd.nextInt(windowheight)/2;
                                        System.out.println(randomX + ": " + windowwidth);
                                        System.out.println(randomY + ": " + windowheight);

                                        vp.topMargin = randomY;
                                        vp.leftMargin = randomX;
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
                                        if(data.get("stickerUrl") != null && data.get("stickerUrl").toString() != "") {
                                            Picasso.get().load(data.get("stickerUrl").toString()).into(sticker);
                                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sticker_bomb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_sticker_bomb:
                Bitmap screenShot = takeScreenshot();
                saveBitmap(screenShot);
                return true;
        }
        return false;
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

    public Bitmap takeScreenshot() {

        progressDialog = ProgressDialog.show(StickerBombPage.this, "",
                "Saving Sticker Bomb...", true);
        View rootView = findViewById(R.id.layout);
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://skatetracker-565c9.appspot.com");

        if(user != null) {
            StorageReference stickerBombRef = storageRef.child("user_data/" + user.getUid() + "_stickerbomb.jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = stickerBombRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    db.collection("users").document(user.getUid()).update("stickerBombUrl", downloadUrl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            startActivity(new Intent(StickerBombPage.this, MainNavigationActivity.class));
                            finish();
                        }
                    });
                }
            });
        }



    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
