package sk8_is_lif3.skatetracker;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class TrickAdapter extends RecyclerView.Adapter<TrickAdapter.ViewHolder> {

    private List<Trick> trickSet;
    private int _expandedPosition = -1, _previousExpandedPosition = -1;
    ViewGroup recyclerView;
    private Trick currentTrick;


    public TrickAdapter(List<Trick> tricks) {
        trickSet = tricks;
    }


    //VIEW HOLDER STUFF
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView trickNameView, ellapsedTimeView, timesLandedView;
        public View itemView;
        public Button startBtn;
        public LinearLayout btnLayout;
        public ImageView removeButton, incrementButton;
        public ViewHolder(View v) {
            super(v);
            itemView = v;
            trickNameView = v.findViewById(R.id.trickName);
            ellapsedTimeView = v.findViewById(R.id.ellapsedTimeCounter);
            startBtn = v.findViewById(R.id.startTrackingButton);
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
                        holder.timesLandedView.setText("Landed: " + trickSet.get(position).GetTimesLanded() + " times");
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
        holder.startBtn.setText(trickSet.get(position).IsTracking()? "Stop Tracking" : "Start Tracking");
        holder.startBtn.setTextColor(trickSet.get(position).IsTracking()?
                                    Color.rgb(255,255,255):
                                    holder.itemView.getResources().getColor(R.color.colorAccent));

        cardView.setCardElevation(isExpanded?8:2);
        holder.incrementButton.animate().translationX(isExpanded?-125:0).setDuration(200);
        cardView.setActivated(isExpanded);

        handler.postDelayed(rn, 0);

        //Button Handlers
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                // Add the buttons
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Are you sure you want to remove this trick?")
                        .setTitle("Remove Trick?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TransitionManager.beginDelayedTransition(recyclerView);
                        trickSet.remove(trickSet.get(position));
                        _expandedPosition = isExpanded ? -1 : position;
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create();
                builder.show();
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
        holder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!trickSet.get(position).IsTracking()) {

                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent((holder.itemView.getContext()), this.getClass());
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    Intent intenttrick = new Intent(context, ActionReceiver.class);
                    Intent intentSession = new Intent(context, CurrentSession.class);
                    intentSession.addFlags(Intent.FLAG_FROM_BACKGROUND);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intenttrick, PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntent pendingSessionIntent = PendingIntent.getActivity(context, 0, intentSession, PendingIntent.FLAG_UPDATE_CURRENT);

                    String channelId = "default_channel_id";

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId);
                    mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
                    mBuilder.setSmallIcon(R.drawable.ic_healing_black_24dp);
                    mBuilder.setContentTitle("Active Session");
                    mBuilder.addAction(R.drawable.ic_plus_1, "Landed", pendingIntent);
                    mBuilder.setContentText("Trick: " + holder.trickNameView.getText().toString());
                    mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                    mBuilder.setContentIntent(pendingSessionIntent);
                    mBuilder.setAutoCancel(false);

                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    mBuilder.setSound(alarmSound);
                    mBuilder.setOngoing(true);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    NotificationManager mNotificationManager = (NotificationManager) recyclerView.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        NotificationChannel mChannel = new NotificationChannel(channelId, "SkateTracker", NotificationManager.IMPORTANCE_HIGH);
                        mNotificationManager.createNotificationChannel(mChannel);
                        mBuilder.setChannelId(channelId);
                    }

                    notificationManager.notify(GenerateID(),mBuilder.build());
                    SetCurrentTrick(trickSet.get(position));
                    for (int i = 0; i < trickSet.size(); i++) {
                        if (trickSet.get(i).IsTracking()) {
                            trickSet.get(i).PauseTracking();
                            notifyItemChanged(i);
                            break;
                        }
                    }
                    trickSet.get(position).StartTracking();
                    notifyItemChanged(position);
                    return;
                }else{
                    NotificationManager mNotificationManager = (NotificationManager) holder.itemView.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancelAll();
                    trickSet.get(position).PauseTracking();
                    if(currentTrick == trickSet.get(position))
                        currentTrick = null;
                    notifyItemChanged(position);
                    return;
                }
            }
        });

    }

    public Trick GetCurrentTrick(){ return currentTrick; }
    public void SetCurrentTrick(Trick cTrick ){ currentTrick = cTrick; }

    @Override
    public int getItemCount() {
        return trickSet.size();
    }

    private int GenerateID() {
        //CREATE STATIC ID
        String ret = "";
        final String digits = "0123456789";
        final String alphanum = digits;
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int randIndex = Math.abs(random.nextInt()) % alphanum.length();
            char lett = alphanum.charAt(randIndex);
            ret += Character.toString(lett);
        }
        return Integer.parseInt(ret);
    }



}
