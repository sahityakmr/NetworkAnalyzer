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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import the_anonymous_koder_2.networkanalyzer.Adapter.OperatorsAdapter;
import the_anonymous_koder_2.networkanalyzer.Model.Operator;

public class OperatorFragment extends Fragment {

    String TAG = "Operator Fragment";
    RecyclerView recyclerView;
    Context context;
    DatabaseReference rootRef;

    ArrayList<Operator> operators;
    View view;
    public OperatorFragment() {
    }

    public static OperatorFragment newInstance() {
        return new OperatorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        context = getContext();
        rootRef = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.operators_rv);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        setAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_operator, container, false);
        initViews();
        return view;
    }

    private void setAdapter() {
        operators = new ArrayList<>();
        rootRef.child(getString(R.string.firebase_operators)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {
                    operators.add(new Operator(snapshot.child("logo").getValue().toString(),snapshot.child("name").getValue().toString()));
                }
                Log.d(TAG, "onDataChange: Operator's Size "+operators.size());
                OperatorsAdapter adapter = new OperatorsAdapter(operators,context);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
