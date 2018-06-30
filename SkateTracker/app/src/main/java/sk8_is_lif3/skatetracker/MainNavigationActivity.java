package sk8_is_lif3.skatetracker;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainNavigationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 123;
    private TextView mTextMessage;
    private Fragment sessions, learn, skate, profile;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_sessions:
                    setTitle("My Sessions");
                    if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                        startActivity(new Intent(MainNavigationActivity.this, LoginActivity.class));
                        finish();
                    }
                    Bundle sArgs = new Bundle();
                    sArgs.putString("tag", "SESSIONS");

                    if(getSupportFragmentManager().findFragmentByTag(sessions.getClass().getName()) != null){
                        System.out.println("FOUND SESSIONS");
                    }
                    replaceFragment(sessions);
                    return true;
                case R.id.navigation_learn:
                    setTitle("Learn");
                    if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                        startActivity(new Intent(MainNavigationActivity.this, LoginActivity.class));
                        finish();
                    }
                    //LimitNumberOfFragments();
                    Bundle lArgs = new Bundle();
                    lArgs.putString("tag", "LEARN");
                    replaceFragment(learn);
                    return true;
                case R.id.navigation_skate:
                    setTitle("S.K.A.T.E");
                    if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                        startActivity(new Intent(MainNavigationActivity.this, LoginActivity.class));
                        finish();
                    }
                    Bundle skArgs = new Bundle();
                    skArgs.putString("tag", "SKATE");

                    replaceFragment(skate);
                    return true;
                    /*
                case R.id.navigation_spots:
                    return true;
                    */
                case R.id.navigation_profile:
                    setTitle("My Profile");
                    if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                        startActivity(new Intent(MainNavigationActivity.this, LoginActivity.class));
                        finish();
                    }
                    Bundle pArgs = new Bundle();
                    pArgs.putString("tag", "PROFILE");
                    replaceFragment(profile);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_navigation);

        BottomNavigationViewEx navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(2).setChecked(true);
        navigation.enableShiftingMode(false);

        sessions = new SessionList();
        skate = new SkateHome();
        profile = new Profile();
        learn = new LearnHome();

        replaceFragment(sessions);

    }
    @Override
    public void onStart(){
        super.onStart();
        // Choose authentication providers
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainNavigationActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
                            } else {
                                Map<String, Object> newUser = new HashMap<String, Object>();
                                newUser.put("name", user.getDisplayName());
                                newUser.put("challenges", new ArrayList<>());
                                newUser.put("achievements", new ArrayList<>());
                                db.collection("users").document(user.getUid()).set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                            }
                        } else {

                        }
                    }
                });
                // ...
            } else {
                if(response.getError() != null){
                    Toast.makeText(getApplicationContext(), response.getError().getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private void replaceFragment(Fragment fragment){
        String backStateName = fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();

        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        FragmentTransaction ft = manager.beginTransaction();
        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
            ft.replace(R.id.fragment, fragment, backStateName);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }else{
            ft.show(fragment);
        }
    }
}