package sk8_is_lif3.skatetracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import sk8_is_lif3.skatetracker.transitions.Achievement;



public class Profile extends Fragment {

    FirebaseFirestore db;
    FirebaseUser user;
    private TextView tv;
    private String holder = "fghjfghj";

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        if(user != null) {
            tv.setText(user.getDisplayName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        setReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        setReenterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        tv = (TextView)v.findViewById(R.id.welcomename);
        return v;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.the_achivements:

                        Intent intent = new Intent(getContext(), AchievementActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        getActivity().startActivity(intent);
                        break;
            case R.id.the_sign_out:
                        if(FirebaseAuth.getInstance().getCurrentUser() != null){
                            AuthUI.getInstance()
                                    .signOut(getActivity())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // ...
                                            Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        break;
        }
        return true;
    }
}
