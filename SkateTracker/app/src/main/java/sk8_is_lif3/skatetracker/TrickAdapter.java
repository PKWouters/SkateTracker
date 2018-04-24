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

import java.util.List;

public class TrickAdapter extends RecyclerView.Adapter<TrickAdapter.ViewHolder> {

    private List<Trick> trickSet;
    private int _expandedPosition = -1, _previousExpandedPosition = -1;
    ViewGroup recyclerView;

    public TrickAdapter(List<Trick> tricks) {
        trickSet = tricks;
    }


    //VIEW HOLDER STUFF
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView trickNameView, ellapsedTimeView, timesLandedView;
        public View itemView;
        public Button startBtn, stopBtn;
        public LinearLayout btnLayout;
        public ImageView removeButton, incrementButton;
        public ViewHolder(View v) {
            super(v);
            itemView = v;
            trickNameView = v.findViewById(R.id.sessionName);
            ellapsedTimeView = v.findViewById(R.id.ellapsedTimeCounter);
            startBtn = v.findViewById(R.id.startTrackingButton);
            stopBtn = v.findViewById(R.id.stopTrackingButton);
            btnLayout = v.findViewById(R.id.buttonLayout);
            removeButton = v.findViewById(R.id.arrowView);
            incrementButton = v.findViewById(R.id.incrementButton);
            timesLandedView = v.findViewById(R.id.timesLandedView);
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
        holder.trickNameView.setTextColor(Color.rgb(255,255,255));
        holder.timesLandedView.setTextColor(Color.rgb(255,255,255));
        holder.timesLandedView.setText("Landed: " + trickSet.get(position).GetTimesLanded() + " times");

        final Handler handler = new Handler();
        final Runnable rn = new Runnable() {
            @Override
            public void run() {
                if(position < trickSet.size())
                    if(isExpanded && trickSet.get(position).IsTracking()){
                        holder.ellapsedTimeView.setText(trickSet.get(position).EllapsedTime());
                        handler.postDelayed(this, 1000);
                    }
            }
        };
        if(isExpanded)
            _previousExpandedPosition = position;

        holder.ellapsedTimeView.setText(trickSet.get(position).EllapsedTime());
        holder.ellapsedTimeView.setTextColor(Color.rgb(255,255,255));
        holder.ellapsedTimeView.setVisibility(isExpanded&&trickSet.get(position).IsTracking()?View.VISIBLE:View.GONE);
        holder.timesLandedView.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.btnLayout.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.removeButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);

        //Is Tracking
        cardView.setCardBackgroundColor(trickSet.get(position).IsTracking()?
                                        holder.itemView.getResources().getColor(R.color.colorAccent):
                                        holder.itemView.getResources().getColor(R.color.colorPrimaryLight));
        holder.startBtn.setTextColor(trickSet.get(position).IsTracking()?
                                    Color.rgb(255,255,255):
                                    holder.itemView.getResources().getColor(R.color.colorAccent));
        holder.stopBtn.setTextColor(trickSet.get(position).IsTracking()?
                Color.rgb(255,255,255):
                holder.itemView.getResources().getColor(R.color.colorAccent));

        cardView.setCardElevation(isExpanded?6:0);
        holder.incrementButton.animate().translationX(isExpanded?-125:0).setDuration(200);
        cardView.setActivated(isExpanded);

        handler.postDelayed(rn, 0);

        //Button Handlers
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(recyclerView);
                trickSet.remove(trickSet.get(position));
                _expandedPosition = isExpanded ? -1:position;
                notifyDataSetChanged();
            }
        });
        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trickSet.get(position).IncrementTimesLanded();
                if(trickSet.get(position).IsTracking())
                    Toast.makeText(holder.itemView.getContext(), "Landed: " + trickSet.get(position).GetTimesLanded(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(holder.itemView.getContext(), "You must be tracking this trick in order to land it...", Toast.LENGTH_SHORT).show();
                notifyItemChanged(position);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _expandedPosition = isExpanded ? -1:position;
                notifyItemChanged(_previousExpandedPosition);
                notifyItemChanged(position);
            }
        });
        holder.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trickSet.get(position).PauseTracking();
                notifyItemChanged(position);
            }
        });
        holder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Trick t:trickSet) {
                    if(t.IsTracking())
                        t.PauseTracking();
                }
                trickSet.get(position).StartTracking();
                notifyItemChanged(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trickSet.size();
    }



}
