package the_anonymous_koder_2.networkanalyzer.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import the_anonymous_koder_2.networkanalyzer.Database.Database;
import the_anonymous_koder_2.networkanalyzer.Model.InfoPacket;
import the_anonymous_koder_2.networkanalyzer.R;
import the_anonymous_koder_2.networkanalyzer.Utils;

/**
 * Created by Sahitya on 3/30/2018.
 */

public class DataPushService extends Service {
    public static String TAG = "Data Push Service";
    public static final int SERVICE_ID_C = 124;
    public static final int SERVICE_ID_N = 124;
    public static final String PUSH_CRASH_REQ = "crash";
    public static final String PUSH_NORMAL_REQ = "normal";
    public static final String REQ_TYPE = "request";
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    Context context;
    Database db;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate() {
        context = this;
        db = new Database();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getExtras()!=null) {
            Log.d(TAG, "onStartCommand: "+intent.getExtras().get(REQ_TYPE));
            if (intent.getExtras().get(REQ_TYPE).equals(PUSH_CRASH_REQ))
                pushCrash();
            if (intent.getExtras().get(REQ_TYPE).equals(PUSH_NORMAL_REQ))
                pushNormal();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void pushNormal() {
        Log.d(TAG, "pushNormal: ");
        Toast.makeText(context, "Pushing Normal Data", Toast.LENGTH_SHORT).show();
        ArrayList<InfoPacket> normalAggregatedInfoPackets = getAggregatedData(db.fetchNormalReports());
        if(normalAggregatedInfoPackets.size()>0)
            uploadPacket(normalAggregatedInfoPackets);
    }

    private void pushCrash() {
        Log.d(TAG, "pushCrash: ");
        Toast.makeText(context, "Pushing Crash Data", Toast.LENGTH_SHORT).show();
        ArrayList<InfoPacket> crashAggregatedInfoPackets = getAggregatedData(db.fetchCrashReports());
        if(crashAggregatedInfoPackets.size()>0)
            uploadPacket(crashAggregatedInfoPackets);
    }

    private void uploadPacket(ArrayList<InfoPacket> infoPackets) {
        for (InfoPacket infoPacket : infoPackets) {
            DatabaseReference packetAddress = rootRef
                    .child(getApplicationContext().getString(R.string.firebase_raw))
                    .child(String.valueOf((int)infoPacket.getLatitude()))
                    .child(String.valueOf((int)infoPacket.getLongitude()))
                    .child(String.valueOf((int)(infoPacket.getLatitude() % 1) * 60))
                    .child(String.valueOf((int)(infoPacket.getLongitude() % 1) * 60))
                    .child(infoPacket.getOperator())
                    .child(infoPacket.getDate_time())
                    .child("mukul(at)gmail(dot)com");
            packetAddress.setValue(infoPacket);
        }
    }

    private ArrayList<InfoPacket> getAggregatedData(ArrayList<InfoPacket> infoPackets) {
        ArrayList<InfoPacket> processedList = new ArrayList<>();
        if(infoPackets.size()>0)
            processedList.add(infoPackets.get(0));
        return processedList;
    }
}
