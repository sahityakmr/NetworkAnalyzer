package the_anonymous_koder_2.networkanalyzer.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import the_anonymous_koder_2.networkanalyzer.Model.InfoPacket;
import the_anonymous_koder_2.networkanalyzer.R;
import the_anonymous_koder_2.networkanalyzer.ViewHolder.ReportViewHolder;

/**
 * Created by Sahitya on 3/26/2018.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportViewHolder> {

    ArrayList<InfoPacket> crashReports;

    public ReportAdapter(ArrayList<InfoPacket> crashReports) {
        this.crashReports = crashReports;
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crash_view,parent,false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {
        holder.operator.setText(crashReports.get(position).getOperator());
        holder.dateTime.setText(crashReports.get(position).getDate_time());
        holder.latitude.setText(""+crashReports.get(position).getLatitude());
        holder.longitude.setText(""+crashReports.get(position).getLongitude());
        holder.area.setText(crashReports.get(position).getArea());
        String qos = "";
        switch (crashReports.get(position).getLevel()){
            case 0:
                qos = "Unknown";
                break;
            case 1:
                qos = "Poor";
                break;
            case 2:
                qos = "Low";
                break;
            case 3:
                qos = "Normal";
                break;
            case 4:
                qos = "Best";
                break;
            default: qos = "Not Available";
        }
        holder.qos.setText(qos);
        holder.signal.setText("Strength(dB) "+crashReports.get(position).getStrength());
        holder.level.setText("Signal Level : "+crashReports.get(position).getLevel());
    }

    @Override
    public int getItemCount() {
        return crashReports.size();
    }
}
