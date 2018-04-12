package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

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
        public TextView sessionNameView, totalTimeView, totalTricksView;
        public View itemView;
        public ImageView removeButton;
        public BarChart barChart;
        public ViewHolder(View v) {
            super(v);
            itemView = v;
            sessionNameView = v.findViewById(R.id.sessionName);
            totalTimeView = v.findViewById(R.id.totalTimePracticed);
            totalTricksView = v.findViewById(R.id.totalTricks);
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
        holder.sessionNameView.setTextColor(Color.WHITE);
        holder.totalTimeView.setTextColor(Color.WHITE);
        holder.totalTimeView.setText("Total Time: " +(int)(currSession.GetHoursTracked()) + " hrs, " + (int)(currSession.GetMinutesTracked()) + "mins, " + (int)(currSession.GetSecondsTracked()) + " secs");
        holder.totalTricksView.setTextColor(Color.WHITE);
        holder.totalTricksView.setText(Integer.toString(currSession.GetTricksAdded().size()) + " tricks practiced");
        holder.removeButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.totalTimeView.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.totalTricksView.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.barChart.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        cardView.setCardElevation(isExpanded?6:0);
        cardView.setActivated(isExpanded);

        //--------------Graph Stuff---------------//
        ArrayList<Trick> tricks = sessionList.get(position).GetTricksAdded();
        final String[] trickNames = new String[tricks.size()];
        for(int i = 0; i < trickNames.length; ++i){
            trickNames[i] = String.valueOf(tricks.get(i).GetName().toUpperCase() + System.getProperty("line.separator") + tricks.get(i).EllapsedTime() + System.getProperty("line.separator") + tricks.get(i).GetTimesLanded() + " Successful Attempts");
        }

        List<BarEntry> entries = new ArrayList<BarEntry>();
        int[] colors = new int[tricks.size()];
        for(int i = 0; i < tricks.size(); ++i){
            System.out.println((Math.round(tricks.get(i).GetRatio())));
            if(tricks.get(i).GetRatio() < 1.0) {
                entries.add(new BarEntry(i, (float) tricks.get(i).GetRatio()));
                colors[i] = Color.RED;
            }else{
                entries.add(new BarEntry(i, (float)(tricks.get(i).GetRatio())));
                colors[i] = Color.GREEN;
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Tricks"); // add entries to dataset
        Description d = new Description();
        d.setText("");
        holder.barChart.setDescription(d);
        holder.barChart.setVisibleXRangeMaximum(4.25f);
        holder.barChart.setScaleEnabled(false);
        dataSet.setLabel("Successful Landings/Minutes");
        dataSet.setColors(colors);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int)(GetGCDNum(value)) + " / " +(int)(GetGCDDen(value)));
            }
        });
        XAxis xAxis = holder.barChart.getXAxis();

        int screenSize = holder.itemView.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                xAxis.setTextSize(8f);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                xAxis.setTextSize(4f);
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                xAxis.setTextSize(2f);
                break;
            default:

        }

        dataSet.setValueTextColor(Color.WHITE);

        BarData lineData = new BarData(dataSet);
        holder.barChart.setData(lineData);
        holder.barChart.invalidate(); // refresh
        holder.barChart.getLegend().setTextColor(Color.WHITE);
        holder.barChart.getLegend().setForm(Legend.LegendForm.LINE);

        IAxisValueFormatter xFormatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if((int)(value) < trickNames.length)
                    return trickNames[(int) value];
                return "TRICK NOT FOUND";
            }


        };

        IAxisValueFormatter yFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return Float.toString(value);
            }
        };

        XAxisRenderer xRenderer = new XAxisRenderer(holder.barChart.getViewPortHandler(), xAxis, holder.barChart.getTransformer(YAxis.AxisDependency.LEFT)){
            @Override
            protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
                String lines[] = formattedLabel.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    float vOffset = i * mAxisLabelPaint.getTextSize();
                    Utils.drawXAxisValue(c, lines[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees);
                }
            }
        };
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormatter);
        holder.barChart.setXAxisRenderer(xRenderer);
        holder.barChart.setExtraBottomOffset(15);
        YAxis yAxis = holder.barChart.getAxisLeft();
        yAxis.setDrawLabels(false); // no axis labels
        yAxis.setDrawAxisLine(false); // no axis line
        yAxis.setDrawGridLines(false); // no grid lines
        yAxis.setDrawZeroLine(true); // draw a zero line
        holder.barChart.getAxisRight().setEnabled(false); // no right axis

        //Button Handlers
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Are you sure you want to remove this Session?")
                        .setTitle("Remove Session")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TransitionManager.beginDelayedTransition(recyclerView);
                                database.sessionDAO().deleteSession(sessionList.get(position));
                                sessionList.remove(sessionList.get(position));
                                _expandedPosition = isExpanded ? -1:position;
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Cancel
                            }
                        });

                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
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

    private double GetGCDDen(double n){
        String s = String.format("%.1f", n);
        int digitsDec = s.length() - 1 - s.indexOf('.');
        int denom = 1;
        for (int i = 0; i < digitsDec; i++) {
            n *= 10;
            denom *= 10;
        }

        int num = (int) Math.round(n);
        double g = gcd(num, denom);
        return (denom /g);
    }

    private double GetGCDNum(double n){
        String s = String.format("%.1f", n);
        int digitsDec = s.length() - 1 - s.indexOf('.');
        int denom = 1;
        for (int i = 0; i < digitsDec; i++) {
            n *= 10;
            denom *= 10;
        }

        int num = (int) Math.round(n);
        double g = gcd(num, denom);
        return (num /g);
    }

    private double gcd(int x, int y){
        int r = x % y;
        while (r != 0){
            x = y;
            y = r;
            r = x % y;
        }
        return y;
    }

}
