package the_anonymous_koder_2.networkanalyzer.ViewHolder;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import the_anonymous_koder_2.networkanalyzer.R;

/**
 * Created by Sahitya on 3/22/2018.
 */

public class OperatorsViewHolder extends RecyclerView.ViewHolder {
    final public SimpleDraweeView operatorLogo;
    final public TextView operatorName;
    final public View itemView;

    public OperatorsViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        operatorLogo = itemView.findViewById(R.id.ov_logo);
        operatorName = itemView.findViewById(R.id.ov_name);
    }
}
