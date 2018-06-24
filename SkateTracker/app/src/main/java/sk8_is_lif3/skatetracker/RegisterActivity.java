package sk8_is_lif3.skatetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "ECH";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.auth_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button emailBtn = (Button) findViewById(R.id.emailSignUpButton);
        Button signInBtn = (Button) findViewById(R.id.signInBtn);
        final EditText nameField = (EditText) findViewById(R.id.nameField);
        final EditText emailField = (EditText) findViewById(R.id.emailField);
        final EditText passwordField = (EditText) findViewById(R.id.passwordField);
        final EditText confirmPasswordField = (EditText) findViewById(R.id.confirmPasswordField);

        ImageView googleBtn = (ImageView) findViewById(R.id.googleButton);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = ProgressDialog.show(RegisterActivity.this, "",
                        "Creating Account...", true);
                final String name = nameField.getText().toString();
                final String email = emailField.getText().toString();
                final String password = passwordField.getText().toString();
                final String confirmPassword = confirmPasswordField.getText().toString();
                if((!name.isEmpty() && name != null) && (!email.isEmpty() && email != null) && (!password.isEmpty() && password != null) && (!confirmPassword.isEmpty() && confirmPassword != null)){

                    if(password.compareTo(confirmPassword) == 0) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        final FirebaseUser user = mAuth.getCurrentUser();
                                        final boolean isNewUser = authResult.getAdditionalUserInfo().isNewUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                            Map<String, Object> newUser = new HashMap<String, Object>();
                                                            newUser.put("name", user.getDisplayName());
                                                            newUser.put("challenges", new ArrayList<>());
                                                            newUser.put("achievements", new ArrayList<>());
                                                            newUser.put("newUser", true);
                                                            db.collection("users").document(user.getUid()).set(newUser)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            if(isNewUser) {
                                                                                progressDialog.dismiss();
                                                                                startActivity(new Intent(RegisterActivity.this, OnboardingActivity.class));
                                                                                finish();
                                                                            }else{
                                                                                Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                                                                                progressDialog.dismiss();
                                                                                startActivity(new Intent(RegisterActivity.this, MainNavigationActivity.class));
                                                                                finish();
                                                                            }
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }else{
                                                            progressDialog.dismiss();
                                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Both Password Fields Must Match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Make Sure All Fields Are Filled In", Toast.LENGTH_SHORT).show();
                }
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(RegisterActivity.this, "",
                        "Creating Account...", true);
                if(mGoogleSignInClient != null){
                    signIn();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            startActivity(new Intent(RegisterActivity.this, MainNavigationActivity.class));
            finish();
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                if(progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            final FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> newUser = new HashMap<String, Object>();
                            newUser.put("name", user.getDisplayName());
                            newUser.put("challenges", new ArrayList<>());
                            newUser.put("achievements", new ArrayList<>());
                            newUser.put("newUser", true);
                            db.collection("users").document(user.getUid()).set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if(isNewUser) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterActivity.this, OnboardingActivity.class));
                                                finish();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterActivity.this, MainNavigationActivity.class));
                                                finish();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
