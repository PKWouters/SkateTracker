package sk8_is_lif3.skatetracker;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
    private FragmentQueue fragmentQueue;

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
                    Fragment sessions = fragmentQueue.findTransaction("SESSIONS");
                    if(sessions == null) {
                        sessions = new SessionList();
                        sessions.setArguments(sArgs);
                    }
                    //LimitNumberOfFragments();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, sessions, "SESSIONS")
                            .commit();
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
                    LearnHome learn = (LearnHome) getSupportFragmentManager().findFragmentByTag("LEARN");
                    if(learn == null) {
                        learn = new LearnHome();
                        learn.setArguments(lArgs);
                    }
                    //LimitNumberOfFragments();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, learn, "LEARN")
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_skate:
                    setTitle("S.K.A.T.E");
                    if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                        startActivity(new Intent(MainNavigationActivity.this, LoginActivity.class));
                        finish();
                    }
                    Bundle skArgs = new Bundle();
                    skArgs.putString("tag", "SKATE");
                    SkateHome skate = (SkateHome) getSupportFragmentManager().findFragmentByTag("SKATE");
                    if(skate == null) {
                        skate = new SkateHome();
                        skate.setArguments(skArgs);
                    }
                    //LimitNumberOfFragments();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, skate, "SKATE")
                            .addToBackStack(null)
                            .commit();
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
                    Profile profile = (Profile) getSupportFragmentManager().findFragmentByTag("PROFILE");
                    if(profile == null) {
                        profile = new Profile();
                        profile.setArguments(pArgs);
                    }
                    //LimitNumberOfFragments();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, profile, "PROFILE")

                            .addToBackStack(null)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentQueue = new FragmentQueue(4);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment session = new SessionList();

        transaction.replace(R.id.fragment, session, "SESSIONS");
        fragmentQueue.addToQueue(session);
        transaction.commit();


        setContentView(R.layout.activity_main_navigation);

        BottomNavigationViewEx navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(2).setChecked(true);
        navigation.enableShiftingMode(false);



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
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        Fragment lastFrag = fragmentQueue.popBack();
        if(lastFrag != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, lastFrag, lastFrag.getTag()).commit();
            return true;
        }
        return false;
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
}

class FragmentQueue{

    ArrayList<Fragment> data;
    int maxSize;

    public FragmentQueue(int maxSize){
        data = new ArrayList<Fragment>();
        this.maxSize = maxSize;
    }

    public Fragment popBack(){
        if(data.size() > 0){
            Fragment ret = data.get(data.size()-1);
            data.remove(data.get(data.size()-1));
            return ret;
        }
        return null;
    }

    public Fragment findTransaction(String tag){
        if (tag != null) {
            // First look through added fragments.
            for (int i=data.size()-1; i>=0; i--) {
                Fragment f = data.get(i);
                if (f != null && tag.equals(f.getTag())) {
                    return f;
                }
            }
        }
        return null;
    }

    public void addToQueue(Fragment object){

        if(data.size() < maxSize){
            data.add(object);
        }else{
            removeFromQueue();
            data.add(object);
        }

    }

    public void removeFromQueue(){
        if(data.size() > 0){
            data.remove(data.get(0));
        }
    }

}
