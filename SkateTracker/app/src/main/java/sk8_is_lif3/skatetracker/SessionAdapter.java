package sk8_is_lif3.skatetracker;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import sk8_is_lif3.skatetracker.database.AppDatabase;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<Session> sessionList;
    private int _expandedPosition = -1;
    ViewGroup recyclerView;
    AppDatabase database;

    public SessionAdapter(List<Session> sessions) {
        sessionList = sessions;
    }


    //VIEW HOLDER STUFF
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView sessionNameView, totalTimeView;
        public View itemView;
        public ImageView removeButton;
        public BarChart barChart;
        public ViewHolder(View v) {
            super(v);
            itemView = v;
            sessionNameView = v.findViewById(R.id.sessionName);
            totalTimeView = v.findViewById(R.id.totalTimePracticed);
            removeButton = v.findViewById(R.id.removeButton);
            barChart = v.findViewById(R.id.sessionChart);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_card_layout, parent, false);
        recyclerView = parent;
        database = AppDatabase.getDatabase(recyclerView.getContext());

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CardView cardView = holder.itemView.findViewById(R.id.card_view);
        final boolean isExpanded = position == _expandedPosition;
        Session currSession = sessionList.get(position);
        holder.sessionNameView.setText(currSession.GetDate());
        holder.sessionNameView.setTextColor(Color.rgb(255,255,255));
        holder.totalTimeView.setTextColor(Color.rgb(255,255,255));
        holder.totalTimeView.setText("Total Time: " +(int)(currSession.GetHoursTracked()) + " hrs, " + (int)(currSession.GetMinutesTracked()) + "mins, " + (int)(currSession.GetSecondsTracked()) + " secs");
        holder.removeButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.totalTimeView.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.barChart.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        cardView.setCardElevation(isExpanded?6:0);
        cardView.setActivated(isExpanded);

        //Graph Stuff
        ArrayList<Trick> tricks = sessionList.get(position).GetTricksAdded();

        List<BarEntry> entries = new ArrayList<BarEntry>();

        for(int i = 0; i < tricks.size(); ++i){
            entries.add(new BarEntry(i+1, tricks.get(i).GetTimesLanded(), tricks.get(i).GetName()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Tricks"); // add entries to dataset
        dataSet.setColor(Color.rgb(255,255,255));
        dataSet.setValueTextColor(Color.rgb(255,255,255)); // styling, ...

        BarData lineData = new BarData(dataSet);
        holder.barChart.setData(lineData);
        holder.barChart.invalidate(); // refresh

        //Button Handlers
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(recyclerView);
                database.sessionDAO().deleteSession(sessionList.get(position));
                sessionList.remove(sessionList.get(position));
                _expandedPosition = isExpanded ? -1:position;
                notifyDataSetChanged();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(recyclerView);
                _expandedPosition = isExpanded ? -1:position;
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }



}
