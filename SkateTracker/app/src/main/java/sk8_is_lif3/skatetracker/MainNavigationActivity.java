package sk8_is_lif3.skatetracker;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
                if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Sign In Cancelled", Toast.LENGTH_SHORT).show();
                    //finish();
                }else if(response.getError() != null){
                    Toast.makeText(getApplicationContext(), response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        }
    }


}
