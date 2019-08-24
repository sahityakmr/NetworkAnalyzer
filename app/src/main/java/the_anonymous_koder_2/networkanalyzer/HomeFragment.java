package the_anonymous_koder_2.networkanalyzer;


import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import the_anonymous_koder_2.networkanalyzer.Database.Database;
import the_anonymous_koder_2.networkanalyzer.Extras.Constants;
import the_anonymous_koder_2.networkanalyzer.Interface.AsyncResponse;
import the_anonymous_koder_2.networkanalyzer.Model.InfoPacket;
import the_anonymous_koder_2.networkanalyzer.Service.NetworkService;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment {
    private static final int GPS_PERMISSION_REQ = 101;
    Context context;
    View view;
    Switch aSwitch;
    Intent serviceIntent;
    String TAG = "Home Fragment";
    Database db;
    public static TextView latLongView;
    ImageView gpsIndicator, dataIndicator, serviceIndicator;
    TextView qosView, signalAsuView, signalDbView,  signalLevelView, opOneView, opTwoView;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private PointerSpeedometer pointerSpeedometer;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        serviceIntent = new Intent(context, NetworkService.class);
        db = new Database();
        initViews();
        initValues();
        return view;
    }

    private void initViews() {
        pointerSpeedometer= view.findViewById(R.id.pointerSpeedometer);
        qosView = view.findViewById(R.id.home_qos);
        signalAsuView = view.findViewById(R.id.home_signal_asu);
        signalDbView = view.findViewById(R.id.home_signal_db);
        signalLevelView = view.findViewById(R.id.home_signal_percent);
        latLongView = view.findViewById(R.id.home_lat_long);
        opOneView = view.findViewById(R.id.home_op_one);
        aSwitch = view.findViewById(R.id.home_switch);
        if(isMyServiceRunning(NetworkService.class))
            aSwitch.setChecked(true);
        else
            aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    Log.d(TAG, "onCheckedChanged: ");
                    if (checkGPSPermission()) {
                        Log.d(TAG, "onCheckedChanged: gps available");
                        if (!NetworkService.IS_SERVICE_RUNNING) {
                            Log.d(TAG, "onCheckedChanged: service not running");
                            serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                            NetworkService.IS_SERVICE_RUNNING = true;
                        }
                    } else {
                        Log.d(TAG, "onCheckedChanged: req GPS");
                        reqGPSPermission();
                    }

                    Toast.makeText(context, "Service Start Button", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onCheckedChanged: unchecked");
                    if (NetworkService.IS_SERVICE_RUNNING) {
                        Log.d(TAG, "onCheckedChanged: service already running");
                        serviceIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                        NetworkService.IS_SERVICE_RUNNING = false;
                        Toast.makeText(context, "Service Stop Button", Toast.LENGTH_SHORT).show();
                    }
                }
                context.startService(serviceIntent);
            }
        });

        final ArrayList<InfoPacket> infoPackets = new ArrayList<>();
        view.findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*int lat_deg,lon_deg,lat_min,lon_min,level;
                //for (int i = 0; i < 100; i++) {
                    lat_deg = (int )(Math.random() * 28 + 8);
                    lon_deg = (int )(Math.random() * 21 + 71);
                    lat_min = (int )(Math.random() * 60 + 1);
                    lon_min = (int )(Math.random() * 60 + 1);
                    level = (int )(Math.random() * 4 + 0);
                    Log.d(TAG, "onClick: "+lat_deg+" "+lon_deg+" "+lat_min+lon_min);
                    rootRef.child(context.getString(R.string.firebase_raw))
                            .child(String.valueOf(lat_deg))
                            .child(String.valueOf(lon_deg))
                            .child(String.valueOf(lat_min))
                            .child(String.valueOf(lon_min))
                            .child("Jio 4G")
                            .child("2018/03/31")
                            .push().setValue(new InfoPacket(new SimpleDateFormat("dd'/'MM'/'yyyy HH:mm:ss").format(new Date()),lat_deg+(lat_min/60)*100,lon_deg+(lon_min/60)*100,"Madhav Puram","Jio 4G",-102,level));
              //  }*/
            db.open();
            db.clearNormalReports();
            db.close();
            android.app.AlertDialog.Builder a = new android.app.AlertDialog.Builder(context);
                a.setIcon(android.R.drawable.ic_dialog_info);
                a.setTitle("Map Guidelines");
                a.setMessage("Local Database Cleared");
                a.show();
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void initValues() {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        opOneView.setText(manager.getNetworkOperatorName());
        int level = 0,strength = 0,levelInAsu = 0;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<CellInfo> cellInfos = null;   //This will give info of all sims present inside your mobile
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "initValues: Permission not Granted");
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cellInfos = manager.getAllCellInfo();
            if(cellInfos!=null){
                for (int i = 0 ; i<cellInfos.size(); i++){
                    Log.d(TAG, "Cell Info for : "+i);
                    if (cellInfos.get(i).isRegistered()){
                        Log.d(TAG, i+ " is registered also");
                        if(cellInfos.get(i) instanceof CellInfoWcdma){
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) manager.getAllCellInfo().get(i);
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                            level = cellSignalStrengthWcdma.getLevel();
                            strength = cellSignalStrengthWcdma.getDbm();
                            levelInAsu = cellSignalStrengthWcdma.getAsuLevel();
                            Log.d(TAG, "sendLocation: cellinfo "+i+" detected in WCDMA");
                        }else if(cellInfos.get(i) instanceof CellInfoGsm){
                            CellInfoGsm cellInfogsm = (CellInfoGsm) manager.getAllCellInfo().get(i);
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                            level = cellSignalStrengthGsm.getLevel();
                            strength = cellSignalStrengthGsm.getDbm();
                            levelInAsu = cellSignalStrengthGsm.getAsuLevel();
                            Log.d(TAG, "sendLocation: cellinfo "+i+" detected in GSM");
                        }else if(cellInfos.get(i) instanceof CellInfoLte){
                            CellInfoLte cellInfoLte = (CellInfoLte) manager.getAllCellInfo().get(i);
                            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                            level = cellSignalStrengthLte.getLevel();
                            strength = cellSignalStrengthLte.getDbm();
                            levelInAsu = cellSignalStrengthLte.getAsuLevel();
                            Log.d(TAG, "sendLocation: cellinfo "+i+" detected in LTE");
                        }
                        Log.d(TAG, "For CellInfo: : "+i+" Signal Strength : "+strength+ " Signal Level : "+level+ " Signal ASU Level : "+levelInAsu);
                        if(i==0){
                            signalLevelView.setText((level*25)+" %");
                            pointerSpeedometer.speedTo(level*25,1);
                            signalDbView.setText(strength+" (dB)");
                            signalAsuView.setText(levelInAsu+" (asu)");
                            switch (level){
                                case 0:
                                    qosView.setText("Quality of Service : Unknown");
                                    break;
                                case 1:
                                    qosView.setText("Quality of Service : Poor");
                                    break;
                                case 2:
                                    qosView.setText("Quality of Service : Normal");
                                    break;
                                case 3:
                                    qosView.setText("Quality of Service : Good");
                                    break;
                                case 4:
                                    qosView.setText("Quality of Service : Excellent");
                                    break;
                            }

                        }
                    }
                }
            }
        }


    }

    public boolean checkGPSPermission(){
        int gpsResult1 = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int gpsResult2 = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        return gpsResult1 == PackageManager.PERMISSION_GRANTED && gpsResult2 == PackageManager.PERMISSION_GRANTED;
    }

    public void reqGPSPermission(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},GPS_PERMISSION_REQ);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case GPS_PERMISSION_REQ:
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted) {
                        Toast.makeText(context, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                        if (!NetworkService.IS_SERVICE_RUNNING) {
                            serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                            NetworkService.IS_SERVICE_RUNNING = true;
                        }
                    }else {
                        Toast.makeText(context, "Permission not Granted so Cannot Start Service", Toast.LENGTH_SHORT).show();
                    }
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
                            showMessageOKCancel("You Need to all Location Permissions", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                        requestPermissions(new String[]{ACCESS_FINE_LOCATION},GPS_PERMISSION_REQ);
                                    }
                                }
                            });
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("Ok",okListener)
                .setNegativeButton("Cancel",null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
