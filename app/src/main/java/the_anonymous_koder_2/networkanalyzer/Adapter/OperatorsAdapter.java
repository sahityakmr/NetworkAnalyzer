package the_anonymous_koder_2.networkanalyzer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import the_anonymous_koder_2.networkanalyzer.Activity.MapsActivity;
import the_anonymous_koder_2.networkanalyzer.Model.Operator;
import the_anonymous_koder_2.networkanalyzer.R;
import the_anonymous_koder_2.networkanalyzer.ViewHolder.OperatorsViewHolder;

/**
 * Created by Sahitya on 3/22/2018.
 */

public class OperatorsAdapter extends RecyclerView.Adapter<OperatorsViewHolder> {

    ArrayList<Operator> operators;
    String TAG = "Operator Adapter";
    Context context;
    public OperatorsAdapter(ArrayList<Operator> operators, Context context) {
        this.operators = operators;
        this.context = context;
    }

    @Override
    public OperatorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.operator_view,parent,false);
        return new OperatorsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OperatorsViewHolder holder, final int position) {
        holder.operatorLogo.setImageURI(Uri.parse(operators.get(position).getLogo()));
        Log.d(TAG, "onBindViewHolder: "+operators.get(position).getLogo());
        holder.operatorName.setText(operators.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(context, MapsActivity.class);
                mapIntent.putExtra(context.getString(R.string.firebase_operators), operators.get(position).getName());
                context.startActivity(mapIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return operators.size();
    }
}
