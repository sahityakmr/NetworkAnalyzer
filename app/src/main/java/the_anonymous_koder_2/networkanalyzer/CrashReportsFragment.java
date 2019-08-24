package the_anonymous_koder_2.networkanalyzer;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;

import the_anonymous_koder_2.networkanalyzer.Adapter.ReportAdapter;
import the_anonymous_koder_2.networkanalyzer.Database.Database;
import the_anonymous_koder_2.networkanalyzer.Model.InfoPacket;

public class CrashReportsFragment extends Fragment {

    Context context;
    String TAG = "Crash Fragment";
    DatabaseReference rootRef;
    RecyclerView recyclerView;
    View view;

    Database db;

    ArrayList<InfoPacket> crashReports;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static CrashReportsFragment newInstance(){
        return new CrashReportsFragment();
    }

    public CrashReportsFragment() {
        // Required empty public constructor
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        db = new Database();
        context = getContext();
        rootRef = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.crash_rv);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ReportAdapter(db.fetchCrashReports()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crash_reports, container, false);
        initViews();
        return view;
    }

}
