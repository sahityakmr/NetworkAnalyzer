package the_anonymous_koder_2.networkanalyzer.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import the_anonymous_koder_2.networkanalyzer.R;

/**
 * Created by Sahitya on 3/26/2018.
 */

public class ReportViewHolder extends RecyclerView.ViewHolder {
    public View itemView;
    public TextView operator;
    public TextView dateTime;
    public TextView latitude;
    public TextView longitude;
    public TextView area;
    public TextView qos;
    public TextView signal;
    public TextView level;
    public ReportViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        operator = itemView.findViewById(R.id.cv_operator);
        dateTime = itemView.findViewById(R.id.cv_date_time);
        latitude = itemView.findViewById(R.id.cv_lat);
        longitude = itemView.findViewById(R.id.cv_long);
        area = itemView.findViewById(R.id.cv_area);
        qos = itemView.findViewById(R.id.cv_qos);
        signal = itemView.findViewById(R.id.cv_signal);
        level = itemView.findViewById(R.id.cv_level);
    }
}
