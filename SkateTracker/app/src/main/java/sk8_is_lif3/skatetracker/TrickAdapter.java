package sk8_is_lif3.skatetracker;

import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TrickAdapter extends RecyclerView.Adapter<TrickAdapter.ViewHolder> {

    private List<Trick> trickSet;
    private int _expandedPosition = -1;
    ViewGroup recyclerView;

    public TrickAdapter(List<Trick> tricks) {
        trickSet = tricks;
    }


    //VIEW HOLDER STUFF
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView trickNameView, ellapsedTimeView;
        public View itemView;
        public Button startBtn, stopBtn;
        public LinearLayout btnLayout;
        public ImageView arrowView;
        public ViewHolder(View v) {
            super(v);
            itemView = v;
            trickNameView = v.findViewById(R.id.trickName);
            ellapsedTimeView = v.findViewById(R.id.ellapsedTimeCounter);
            startBtn = v.findViewById(R.id.startTrackingButton);
            stopBtn = v.findViewById(R.id.stopTrackingButton);
            btnLayout = v.findViewById(R.id.buttonLayout);
            arrowView = v.findViewById(R.id.arrowView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trick_card_layout, parent, false);
        recyclerView = parent;
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CardView cardView = holder.itemView.findViewById(R.id.card_view);
        final boolean isExpanded = position == _expandedPosition;
        holder.trickNameView.setText(trickSet.get(position).GetName());
        holder.ellapsedTimeView.setText(trickSet.get(position).EllapsedTime());
        holder.ellapsedTimeView.setVisibility(isExpanded&&trickSet.get(position).IsTracking()?View.VISIBLE:View.GONE);
        holder.btnLayout.setVisibility(isExpanded?View.VISIBLE:View.GONE);


        //Is Tracking
        cardView.setCardBackgroundColor(trickSet.get(position).IsTracking()?
                                        holder.itemView.getResources().getColor(R.color.colorAccent):
                                        holder.itemView.getResources().getColor(R.color.cardview_dark_background));
        holder.startBtn.setTextColor(trickSet.get(position).IsTracking()?
                                    Color.rgb(255,255,255):
                                    holder.itemView.getResources().getColor(R.color.colorAccent));
        holder.stopBtn.setTextColor(trickSet.get(position).IsTracking()?
                Color.rgb(255,255,255):
                holder.itemView.getResources().getColor(R.color.colorAccent));

        cardView.setCardElevation(isExpanded?6:0);
        cardView.setCardElevation(trickSet.get(position).IsTracking()?3:0);
        holder.itemView.setActivated(isExpanded);

        final Handler handler = new Handler();
        final Runnable rn = new Runnable() {
            @Override
            public void run() {
                if(isExpanded && trickSet.get(position).IsTracking()){
                    holder.ellapsedTimeView.setText(trickSet.get(position).EllapsedTime());
                    handler.postDelayed(this, 1000);
                }
            }
        };

        holder.arrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trickSet.remove(trickSet.get(position));
                notifyDataSetChanged();
            }
        });

        //Button Handlers
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(rn, 0);
                TransitionManager.beginDelayedTransition(recyclerView);
                _expandedPosition = isExpanded ? -1:position;
                notifyDataSetChanged();
            }
        });
        holder.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(recyclerView);
                trickSet.get(position).PauseTracking();
                notifyDataSetChanged();
            }
        });
        holder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(recyclerView);
                trickSet.get(position).StartTracking();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return trickSet.size();
    }

}
