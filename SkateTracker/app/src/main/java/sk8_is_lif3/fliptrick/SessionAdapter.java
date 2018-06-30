package sk8_is_lif3.fliptrick;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import java.util.List;
import java.util.Map;


public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<String> sessionList;
    private int _expandedPosition = -1;
    private int _previousExpandedPosition = -1;
    ViewGroup recyclerView;
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
            sessionNameView = v.findViewById(R.id.trickName);
            totalTimeView = v.findViewById(R.id.totalTimePracticed);
            totalTricksView = v.findViewById(R.id.totalTricks);
            barChart = v.findViewById(R.id.sessionChart);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_card_layout, parent, false);
        recyclerView = parent;
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
