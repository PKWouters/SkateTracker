package sk8_is_lif3.skatetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TrickDetailFragment extends Fragment{

    private String mName, mId;
    private double mRatio;
    private ArrayList<Map<String, Object>> mSessions;

    private LineChart lineChart;
    private TextView totalTimeView, totalTricksView;

    public TrickDetailFragment() {
        // Required empty public constructor
    }

    public TrickDetailFragment newInstance(String name, String avgRatio, ArrayList<Map<String, Object>> sessions){

        TrickDetailFragment fragment = new TrickDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ValidFragment")
    public TrickDetailFragment(String name, double ratio, String id, ArrayList<Map<String, Object>> sessions) {
        mName = name;
        mRatio = ratio;
        mSessions = sessions;
        mId = id;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //getView().findViewById(R.id.toolbar_title).setTransitionName("sessionNameTransition"+mId);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        final Button learnButton = getView().findViewById(R.id.learnButton);

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);

        TextView sessionName = getView().findViewById(R.id.trickName);
        sessionName.setText(mName);
        sessionName.setTextColor(Color.WHITE);

        TextView progressText = getView().findViewById(R.id.trickProgressName);

        DonutProgress progress = (DonutProgress) getView().findViewById(R.id.trickProgress);
        int ratioToInt = (int)(mRatio*100);
        if(ratioToInt > 100)
            ratioToInt = 100;
        progress.setDonut_progress(Integer.toString(ratioToInt));
        if(ratioToInt < 100){
            progressText.setText("Progress: LEARNING");
            if(mId.contains("trick_")) {
                TextView learnText = getView().findViewById(R.id.learningDescView);
                learnText.setVisibility(View.VISIBLE);
                learnButton.setVisibility(View.VISIBLE);
            }else{
                TextView learnText = getView().findViewById(R.id.learningDescView);
                learnText.setVisibility(View.GONE);
                learnButton.setVisibility(View.GONE);
            }
            progress.setTextColor(getResources().getColor(R.color.colorAccent));
            progress.setFinishedStrokeColor(getResources().getColor(R.color.colorAccent));
        }else{
            progressText.setText("Progress: MASTERED");
            progress.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            progress.setFinishedStrokeColor(getResources().getColor(android.R.color.holo_green_dark));
            TextView learnText = getView().findViewById(R.id.learningDescView);
            learnText.setVisibility(View.GONE);
            learnButton.setVisibility(View.GONE);
        }

        learnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getView(), "Skate School Coming Soon :)", Snackbar.LENGTH_SHORT).show();
            }
        });





        //--------------GRAPH STUFF---------------//

        lineChart = getView().findViewById(R.id.trickChart);

        if (mSessions != null && mSessions.size() > 0) {

            final String[] trickNames = new String[mSessions.size()];
            for (int i = 0; i < trickNames.length; ++i) {
                trickNames[i] = String.valueOf(mSessions.get(i).get("date") + System.getProperty("line.separator") + mSessions.get(i).get("totalLandings") + " Landings");
            }

            List<Entry> entries = new ArrayList<Entry>();
            for (int i = 0; i < mSessions.size(); ++i) {
                double ratio = Double.parseDouble(mSessions.get(i).get("ratio").toString());
                if (ratio < 1.0) {
                    entries.add(new Entry(i, (float) (ratio)));
                } else {
                    entries.add(new Entry(i, (float) (ratio)));
                }
            }

            LineDataSet dataSet = new LineDataSet(entries, "Sessions"); // add entries to dataset
            dataSet.setLineWidth(2f);
            dataSet.setLabel("");
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setColor(getResources().getColor(R.color.colorAccent));
            dataSet.setCircleColor(getResources().getColor(R.color.colorAccent));
            dataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) (GetGCDNum(value)) + " / " + (int) (GetGCDDen(value)));
                }
            });
            lineChart.setDescription(null);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setGranularity(1f);
            int screenSize = getView().getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;


            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.invalidate(); // refresh
            lineChart.getLegend().setTextColor(Color.WHITE);
            lineChart.getLegend().setForm(Legend.LegendForm.NONE);

            IAxisValueFormatter xFormatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if ((int) (value) < trickNames.length && (int)(value) != -1)
                        return trickNames[(int) value];
                    return "";
                }


            };

            IAxisValueFormatter yFormatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return Float.toString(value);
                }
            };

            XAxisRenderer xRenderer = new XAxisRenderer(lineChart.getViewPortHandler(), xAxis, lineChart.getTransformer(YAxis.AxisDependency.LEFT)) {
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
            lineChart.setXAxisRenderer(xRenderer);
            lineChart.setExtraBottomOffset(15);
            YAxis yAxis = lineChart.getAxisLeft();
            yAxis.setDrawLabels(false); // no axis labels
            yAxis.setDrawAxisLine(false); // no axis line
            yAxis.setDrawGridLines(false); // no grid lines
            yAxis.setDrawZeroLine(true); // draw a zero line
            lineChart.getAxisRight().setEnabled(false); // no right axis
            switch (screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    xAxis.setTextSize(12f);
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    xAxis.setTextSize(8f);
                    break;
                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                    xAxis.setTextSize(4f);
                    break;
                default:


            }
            lineChart.moveViewToX((float)(entries.size()-1));
            lineChart.setScaleEnabled(true);
            lineChart.setScaleYEnabled(false);

        }else{
            lineChart.setVisibility(View.GONE);
        }
        startPostponedEnterTransition();
    }

    private double GetGCDDen(double n) {
        String s = String.format("%.1f", n);
        int digitsDec = s.length() - 1 - s.indexOf('.');
        int denom = 1;
        for (int i = 0; i < digitsDec; i++) {
            n *= 10;
            denom *= 10;
        }

        int num = (int) Math.round(n);
        double g = gcd(num, denom);
        return (denom / g);
    }

    private double GetGCDNum(double n) {
        String s = String.format("%.1f", n);
        int digitsDec = s.length() - 1 - s.indexOf('.');
        int denom = 1;
        for (int i = 0; i < digitsDec; i++) {
            n *= 10;
            denom *= 10;
        }

        int num = (int) Math.round(n);
        double g = gcd(num, denom);
        return (num / g);
    }

    private double gcd(int x, int y) {
        int r = x % y;
        while (r != 0) {
            x = y;
            y = r;
            r = x % y;
        }
        return y;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trick_detail, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private TextView getTitleTextView(Toolbar toolbar) {
        try {
            Class<?> toolbarClass = Toolbar.class;
            Field titleTextViewField = toolbarClass.getDeclaredField("mTitleTextView");
            titleTextViewField.setAccessible(true);
            TextView titleTextView = (TextView) titleTextViewField.get(toolbar);

            return titleTextView;
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
