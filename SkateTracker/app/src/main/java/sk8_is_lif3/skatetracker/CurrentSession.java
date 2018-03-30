package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sk8_is_lif3.skatetracker.database.AppDatabase;

public class CurrentSession extends AppCompatActivity{


    private RecyclerView trickRecyclerView;
    private RecyclerView.Adapter trickAdapter;
    private RecyclerView.LayoutManager trickLayoutManager;
    private AppDatabase database;
    List<Trick> tempTrickList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_session);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Current Session");
        setSupportActionBar(toolbar);

        database = AppDatabase.getDatabase(getApplicationContext());
        tempTrickList = new ArrayList<Trick>();

        if(database.trickDAO().getTricks().size() > 0) {
            for (Trick t:database.trickDAO().getTricks()) {
                if(!tempTrickList.contains(t))
                    tempTrickList.add(t);
            }
        }

        trickRecyclerView = (RecyclerView) findViewById(R.id.trickRecyclerView);
        trickRecyclerView.setHasFixedSize(true);
        trickLayoutManager = new LinearLayoutManager(this);
        trickRecyclerView.setLayoutManager(trickLayoutManager);
        trickAdapter = new TrickAdapter(tempTrickList);
        trickRecyclerView.setAdapter(trickAdapter);


        //ADD NEW TRICK BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the Builder class for convenient dialog construction
                final AlertDialog.Builder builder = new AlertDialog.Builder(CurrentSession.this);
                final LayoutInflater inflater = getLayoutInflater();
                final View dlgView = inflater.inflate(R.layout.add_trick_dialog, null);

                final TextView trickName = (EditText) dlgView.findViewById(R.id.trickNameField);
                builder.setView(dlgView)
                        .setMessage("Add New Trick")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Save to Trick List
                                tempTrickList.add(new Trick(trickName.getText().toString()));
                                trickAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Cancel
                            }
                        });

                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (Trick trick:database.trickDAO().getTricks()) {
            database.trickDAO().deleteTrick(trick);
        }
        for (Trick trick:tempTrickList) {
            database.trickDAO().insertTrick(trick);
        }
    }

}
