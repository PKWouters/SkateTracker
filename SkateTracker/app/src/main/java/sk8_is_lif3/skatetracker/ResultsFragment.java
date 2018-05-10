package sk8_is_lif3.skatetracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Map;

@SuppressLint("ValidFragment")
public class ResultsFragment extends DialogFragment {

    Map<String, String> results;
    int gameMode;
    String winner, player1, player2;

    @SuppressLint("ValidFragment")
    public ResultsFragment(Map<String, String> r, int gM, String w, String p1, String p2){
        results = r;
        winner = w;
        player1 = p1;
        player2 = p2;
        gameMode = gM;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.dialog_results_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TableLayout tbl = (TableLayout) getView().findViewById(R.id.resultsTable);
        TextView p1Name = (TextView)getView().findViewById(R.id.initName);
        TextView p2Name = (TextView)getView().findViewById(R.id.initName2);
        p1Name.setText(player1);
        p2Name.setText(player2);
        if(gameMode == 0) {
            int roundNum = 1;
            for (; roundNum <= results.size(); roundNum++) {
                TableRow tblRow = new TableRow(getContext());
                tblRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView roundNumView = new TextView(getContext());
                roundNumView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                roundNumView.setText("Round: " + roundNum);
                TextView p1Result = new TextView(getContext());
                p1Result.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                if (results.get("Round: " + roundNum) == "p1" || results.get("Round: " + roundNum) == "both")
                    p1Result.setText("LANDED");
                else
                    p1Result.setText("FAILED");
                TextView p2Result = new TextView(getContext());
                p2Result.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                if (results.get("Round: " + roundNum) == "p2" || results.get("Round: " + roundNum) == "both")
                    p2Result.setText("LANDED");
                else
                    p2Result.setText("FAILED");
                tblRow.addView(roundNumView);
                tblRow.addView(p1Result);
                tblRow.addView(p2Result);
                tbl.addView(tblRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }else if(gameMode == 1){
            TextView roundInit = (TextView) getView().findViewById(R.id.initRoundNum);
            roundInit.setText("Trick");
            for(Map.Entry<String, String> trick : results.entrySet()){
                TableRow tblRow = new TableRow(getContext());
                tblRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView roundNumView = new TextView(getContext());
                roundNumView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                roundNumView.setText(trick.getKey().substring(trick.getKey().indexOf('_')+1, trick.getKey().length()));
                TextView p1Result = new TextView(getContext());
                p1Result.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                if (trick.getValue() == "p1" || trick.getValue() == "both")
                    p1Result.setText("LANDED");
                else
                    p1Result.setText("FAILED");
                TextView p2Result = new TextView(getContext());
                p2Result.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                if (trick.getValue() == "p2" || trick.getValue() == "both")
                    p2Result.setText("LANDED");
                else
                    p2Result.setText("FAILED");
                tblRow.addView(roundNumView);
                tblRow.addView(p1Result);
                tblRow.addView(p2Result);
                tbl.addView(tblRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }
        TextView winnerView = (TextView) getView().findViewById(R.id.winnerName);
        winnerView.setText("Winner: " +  winner);

        Button quitBtn = (Button) getView().findViewById(R.id.quitButton);
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainNavigationActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

    }


    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
