package sk8_is_lif3.skatetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SessionDetailFragment extends Fragment{

    private String mName, mTotalTime;
    private ArrayList<Map<String, Object>> mTricks;

    private BarChart barChart;
    private TextView totalTimeView, totalTricksView;

    public SessionDetailFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SessionDetailFragment(String name, String totalTime, ArrayList<Map<String, Object>> tricks) {
        mName = name;
        mTotalTime = totalTime;
        mTricks = tricks;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.setTitle(mName);
        toolbar.setTransitionName("sessionNameTransition");
        toolbar.setNestedScrollingEnabled(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        startPostponedEnterTransition();

        barChart = getView().findViewById(R.id.sessionChart);
        totalTimeView = getView().findViewById(R.id.totalTimePracticed);
        totalTricksView = getView().findViewById(R.id.totalTricks);

        totalTimeView.setText(mTotalTime);
        totalTimeView.setTextColor(Color.WHITE);
        totalTricksView.setTextColor(Color.WHITE);
        if (mTricks != null)
            totalTricksView.setText(mTricks.size() + " tricks practiced");

        if (mTricks != null) {
            //--------------Graph Stuff---------------//

            final String[] trickNames = new String[mTricks.size()];
            for (int i = 0; i < trickNames.length; ++i) {
                trickNames[i] = String.valueOf(mTricks.get(i).get("name") + System.getProperty("line.separator") + mTricks.get(i).get("totalTimeFormatted") + System.getProperty("line.separator") + mTricks.get(i).get("timesLanded") + " Successful Attempts");
            }

            List<BarEntry> entries = new ArrayList<BarEntry>();
            int[] colors = new int[mTricks.size()];
            for (int i = 0; i < mTricks.size(); ++i) {
                double ratio = Double.parseDouble(mTricks.get(i).get("ratio").toString());
                if (ratio < 1.0) {
                    entries.add(new BarEntry(i, (float) (ratio)));
                    colors[i] = Color.RED;
                } else {
                    entries.add(new BarEntry(i, (float) (ratio)));
                    colors[i] = Color.GREEN;
                }
            }

            BarDataSet dataSet = new BarDataSet(entries, "Tricks"); // add entries to dataset
            Description d = new Description();
            d.setText("");
            barChart.setDescription(d);


            dataSet.setLabel("Successful Landings/Minutes");
            dataSet.setColors(colors);
            dataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) (GetGCDNum(value)) + " / " + (int) (GetGCDDen(value)));
                }
            });
            XAxis xAxis = barChart.getXAxis();

            int screenSize = getView().getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;
            switch (screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    xAxis.setTextSize(9f);
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    xAxis.setTextSize(5f);
                    break;
                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                    xAxis.setTextSize(3f);
                    break;
                default:

            }

            dataSet.setValueTextColor(Color.WHITE);

            BarData lineData = new BarData(dataSet);
            barChart.setData(lineData);
            barChart.invalidate(); // refresh
            barChart.getLegend().setTextColor(Color.WHITE);
            barChart.getLegend().setForm(Legend.LegendForm.LINE);

            IAxisValueFormatter xFormatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if ((int) (value) < trickNames.length)
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

            XAxisRenderer xRenderer = new XAxisRenderer(barChart.getViewPortHandler(), xAxis, barChart.getTransformer(YAxis.AxisDependency.LEFT)) {
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
            barChart.setXAxisRenderer(xRenderer);
            barChart.setExtraBottomOffset(15);
            YAxis yAxis = barChart.getAxisLeft();
            yAxis.setDrawLabels(false); // no axis labels
            yAxis.setDrawAxisLine(false); // no axis line
            yAxis.setDrawGridLines(false); // no grid lines
            yAxis.setDrawZeroLine(true); // draw a zero line
            barChart.getAxisRight().setEnabled(false); // no right axis
            barChart.setVisibleXRangeMaximum(4.25f);

        }
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
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_session_detail, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
