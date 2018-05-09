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
    String winner;

    @SuppressLint("ValidFragment")
    public ResultsFragment(Map<String, String> r, String w){
        results = r;
        winner = w;
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
        int roundNum = 1;
        for(; roundNum <= results.size(); roundNum++){
            TableRow tblRow = new TableRow(getContext());
            tblRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView roundNumView = new TextView(getContext());
            roundNumView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            roundNumView.setText("Round: " + roundNum);
            TextView p1Result = new TextView(getContext());
            p1Result.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
            if(results.get("Round: " + roundNum) == "p1" || results.get("Round: " + roundNum) == "both")
                p1Result.setText("LANDED");
            else
                p1Result.setText("FAILED");
            TextView p2Result = new TextView(getContext());
            p2Result.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
            if(results.get("Round: " + roundNum) == "p2" || results.get("Round: " + roundNum) == "both")
                p2Result.setText("LANDED");
            else
                p2Result.setText("FAILED");
            tblRow.addView(roundNumView);
            tblRow.addView(p1Result);
            tblRow.addView(p2Result);
            tbl.addView(tblRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
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
