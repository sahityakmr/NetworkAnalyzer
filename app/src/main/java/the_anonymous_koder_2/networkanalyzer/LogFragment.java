package the_anonymous_koder_2.networkanalyzer;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import the_anonymous_koder_2.networkanalyzer.Adapter.EventLogAdapter;
import the_anonymous_koder_2.networkanalyzer.Database.Database;

public class LogFragment extends Fragment {

    Context context;
    String TAG = "Log Fragment";
    DatabaseReference rootRef;
    RecyclerView recyclerView;
    View view;
    Database db;

    public LogFragment() {
        // Required empty public constructor
    }

    public static LogFragment newInstance() {
        return new LogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        db = new Database();
        context = getContext();
        rootRef = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.operators_rv);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new EventLogAdapter(db.fetchLog()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_operator, container, false);
        initViews();
        return view;
    }

}
