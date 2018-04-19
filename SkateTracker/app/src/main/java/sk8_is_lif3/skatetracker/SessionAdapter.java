package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sk8_is_lif3.skatetracker.database.AppDatabase;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<String> sessionList;
    private int _expandedPosition = -1;
    private int _previousExpandedPosition = -1;
    ViewGroup recyclerView;
    AppDatabase database;
    Map<String, Object> currSession;

    public SessionAdapter(List<String> sessions) {
        sessionList = sessions;
        System.out.println(sessionList);
    }


    //VIEW HOLDER STUFF
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView sessionNameView, totalTimeView, totalTricksView;
        public View itemView;
        public ImageView removeButton;
        public int currPosition;
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
        final ViewHolder vh = new ViewHolder(v);

        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


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
