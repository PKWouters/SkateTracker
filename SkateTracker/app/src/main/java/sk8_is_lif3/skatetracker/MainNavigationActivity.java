package sk8_is_lif3.skatetracker;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainNavigationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 123;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_sessions:
                    setTitle("My Sessions");
                    if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Intent i = AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()
                                        )
                                )
                                .setLogo(R.drawable.ic_account_circle)
                                .setTheme(R.style.AppTheme)
                                .build();
                        startActivityForResult(i, RC_SIGN_IN);
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment, new SessionList(), "SessionsList")
                            .commit();
                    return true;
                case R.id.navigation_spots:
                    return true;
                case R.id.navigation_profile:
                    setTitle("My Profile");
                    if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Intent i = AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()
                                        )
                                )
                                .setLogo(R.drawable.ic_account_circle)
                                .setTheme(R.style.AppTheme)
                                .build();
                        startActivityForResult(i, RC_SIGN_IN);
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment, new Profile(), "Profile")
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, new SessionList(), "SessionsList")
                .commit();

        setContentView(R.layout.activity_main_navigation);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }
    @Override
    public void onStart(){
        super.onStart();
        // Choose authentication providers
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent i = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                            )
                    )
                    .setLogo(R.drawable.ic_account_circle)
                    .setTheme(R.style.AppTheme)
                    .build();
            startActivityForResult(i, RC_SIGN_IN);
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
