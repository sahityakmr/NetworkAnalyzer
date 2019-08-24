package the_anonymous_koder_2.networkanalyzer.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import the_anonymous_koder_2.networkanalyzer.R;

/**
 * Created by Sahitya on 3/26/2018.
 */

public class EventLogViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView dateTime;
    public TextView content;
    public View itemView;
    public EventLogViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        title = itemView.findViewById(R.id.ev_title);
        content = itemView.findViewById(R.id.ev_content);
        dateTime = itemView.findViewById(R.id.ev_date_time);
    }
}
