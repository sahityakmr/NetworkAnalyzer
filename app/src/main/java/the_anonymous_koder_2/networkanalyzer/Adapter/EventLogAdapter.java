package the_anonymous_koder_2.networkanalyzer.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import the_anonymous_koder_2.networkanalyzer.Model.EventLog;
import the_anonymous_koder_2.networkanalyzer.R;
import the_anonymous_koder_2.networkanalyzer.ViewHolder.EventLogViewHolder;

/**
 * Created by Sahitya on 3/26/2018.
 */

public class EventLogAdapter extends RecyclerView.Adapter<EventLogViewHolder > {

    ArrayList<EventLog> events;
    public EventLogAdapter(ArrayList<EventLog> events) {
        this.events = events;
    }

    @Override
    public EventLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view,parent,false);
        return new EventLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventLogViewHolder holder, int position) {
        holder.title.setText(events.get(position).getType());
        holder.dateTime.setText(events.get(position).getDate_time());
        holder.content.setText(events.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
