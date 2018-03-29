package sk8_is_lif3.skatetracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TrickAdapter extends RecyclerView.Adapter<TrickAdapter.ViewHolder> {

    private ArrayList<Trick> trickSet;


    public TrickAdapter(ArrayList<Trick> tricks) {
        trickSet = tricks;
    }


    //VIEW HOLDER STUFF
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public TextView ellapsedTimeView;
        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.trickName);
            ellapsedTimeView = v.findViewById(R.id.ellapsedTimeCounter);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trick_card_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(trickSet.get(position).GetName());
        holder.ellapsedTimeView.setText(Integer.toString(trickSet.get(position).EllapsedTime()));
    }

    @Override
    public int getItemCount() {
        return trickSet.size();
    }

}
