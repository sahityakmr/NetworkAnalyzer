package the_anonymous_koder_2.networkanalyzer.Service;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import the_anonymous_koder_2.networkanalyzer.Database.Database;
import the_anonymous_koder_2.networkanalyzer.Extras.Constants;
import the_anonymous_koder_2.networkanalyzer.HomeFragment;
import the_anonymous_koder_2.networkanalyzer.Interface.AsyncResponse;
import the_anonymous_koder_2.networkanalyzer.Model.InfoPacket;
import the_anonymous_koder_2.networkanalyzer.R;
import the_anonymous_koder_2.networkanalyzer.Utils;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NetworkService extends Service implements LocationListener {

    private String TAG = "Network Service";
    public static boolean IS_SERVICE_RUNNING = false;

    private Context context;

    private String userEmail;
    private String operator;
    private int strength = 0,level = 0;
    public static final int CRASH_SEND_DURATION = 1000*30;
    public static final int REPORT_SEND_DURATION = 1000*20;
    public static final int SIGNAL_COLLECT_DURATION = 1000*20;
    public static final int OFFLINE_WAIT_DURATION = 1000*60*1;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Database database;

    FirebaseAuth auth;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    protected LocationManager locationManager;
    Location location;
    double latitude;
    double longitude;
    Notification notification;
    Intent intent;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        database = new Database();
        context = getApplicationContext();
        getLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        auth = FirebaseAuth.getInstance();
        userEmail = "sahityakmr28(at)gmail(dot)com";
        this.intent = intent;
        initService();
        Log.d(TAG, "onStartCommand: "+ userEmail);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        operator = telephonyManager.getNetworkOperatorName();
        return START_STICKY;
    }

    private void initService() {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Start Foreground Intent ");
            showNotification();
            collectSignalTask();
            crashSenderTask();
            normalSenderTask();
            smsSenderTask();
            Toast.makeText(this, "Service Started!", Toast.LENGTH_SHORT).show();

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i(TAG, "Clicked Previous");
            Toast.makeText(this, "Clicked Previous!", Toast.LENGTH_SHORT)
                    .show();
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i(TAG, "Clicked Play");
            Toast.makeText(this, "Clicked Play!", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i(TAG, "Clicked Next");
            Toast.makeText(this, "Clicked Next!", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
    }

    public void smsSenderTask() {
        Log.d(TAG, "smsSenderTask: ");
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        sendSMS();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, OFFLINE_WAIT_DURATION);
    }

    private void sendSMS() {
        Log.d(TAG, "sendSMS: ");
        Toast.makeText(context, "Sending Data Through SMS", Toast.LENGTH_SHORT).show();
        InfoPacket infoPacket = new InfoPacket(new SimpleDateFormat(context.getString(R.string.date_time)).format(new Date()),latitude,longitude,"Area",operator,strength,level);
        ArrayList<InfoPacket> crashQueue = database.fetchCrashQueue();
        ArrayList<InfoPacket> normalQueue = database.fetchNormalQueue();
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("+918191996071", null, new Gson().toJson(infoPacket,InfoPacket.class), null, null);
            if(crashQueue.size()>0)
                sms.sendTextMessage("+918191996071", null, new Gson().toJson(crashQueue.get(0),InfoPacket.class), null, null);
            if(normalQueue.size()>0)
                sms.sendTextMessage("+918191996071", null, new Gson().toJson(normalQueue.get(0),InfoPacket.class), null, null);
        } catch (Exception e) {
        }

    }

    private void showNotification() {
        Toast.makeText(this, "Inside show Notification!", Toast.LENGTH_SHORT).show();
        Intent notificationIntent = new Intent(this, HomeFragment.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, NetworkService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, NetworkService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, NetworkService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo_main_1);

        notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this)
                    .setContentTitle("Network Analyzer")
                    .setTicker("Network Analyzer")
                    .setContentText("Fetching Strength")
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_lock_power_off, "Exit",
                            ppreviousIntent).build();
        }
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
    }

    public void collectSignalTask() {
        Log.d(TAG, "collectSignalTask: ");
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                        public void run() {
                        try {
                            getLocation();
                            if (!String.valueOf(latitude).equals("0.0") || !String.valueOf(longitude).equals("0.0")) {
                                sendLocation();
                                HomeFragment.latLongView.setText("Latitude : "+latitude+"Longitude : "+longitude);
                            }else{
                                Log.d(TAG, "run: Cannot Get Location");
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, SIGNAL_COLLECT_DURATION);
    }


    public void crashSenderTask() {
        Log.d(TAG, "crashSenderTask: ");
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        pushCrash();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, CRASH_SEND_DURATION);
    }


    public void normalSenderTask() {
        Log.d(TAG, "normalSenderTask: ");
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        pushNormal();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, REPORT_SEND_DURATION);
    }

    private void pushNormal() {
        Log.d(TAG, "pushNormal: ");
        Toast.makeText(context, "Pushing Normal Data", Toast.LENGTH_SHORT).show();
        ArrayList<InfoPacket> normalAggregatedInfoPackets = getAggregatedData(database.fetchNormalReports());
        if(normalAggregatedInfoPackets.size()>0)
            uploadPacket(normalAggregatedInfoPackets,Database.NORMAL_TABLE_QUEUE);
    }

    private void pushCrash() {
        Log.d(TAG, "pushCrash: ");
        Toast.makeText(context, "Pushing Crash Data", Toast.LENGTH_SHORT).show();
        ArrayList<InfoPacket> crashAggregatedInfoPackets = getAggregatedData(database.fetchCrashReports());
        if(crashAggregatedInfoPackets.size()>0)
            uploadPacket(crashAggregatedInfoPackets,Database.CRASH_TABLE_QUEUE);
    }

    private void uploadPacket(ArrayList<InfoPacket> infoPackets, final String table) {
        Log.d(TAG, "uploadPacket: info packet size "+infoPackets.size());
        final int id = (int)System.currentTimeMillis()%1000;
        for (final InfoPacket infoPacket : infoPackets) {
            if((int)infoPacket.getLatitude() == 0 || (int)infoPacket.getLongitude() == 0 || (int)((infoPacket.getLatitude() % 1) * 60) == 0 || (int)((infoPacket.getLongitude() % 1) * 60) == 0)
                return;
            database.open();
            database.addProcessedReports(infoPacket,table,id,Database.QUEUED);
            database.close();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            try {
                date = dateFormat.parse(infoPacket.getDate_time());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DatabaseReference packetAddress = rootRef
                    .child(getApplicationContext().getString(R.string.firebase_raw))
                    .child(String.valueOf((int)infoPacket.getLatitude()))
                    .child(String.valueOf((int)infoPacket.getLongitude()))
                    .child(String.valueOf((int)(((infoPacket.getLatitude() % 1) * 60))))
                    .child(String.valueOf((int)(((infoPacket.getLongitude() % 1) * 60))))
                    .child(infoPacket.getOperator())
                    .child(new SimpleDateFormat("yyyy/MM/dd").format(date));
            packetAddress.push().setValue(infoPacket).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: upload successful" +
                                "" +
                                "");
                        database.open();
                        database.updateProcessedReports(Database.SENT,table);
                        database.close();
                    }
                }
            });
        }
    }

    private ArrayList<InfoPacket> getAggregatedData(ArrayList<InfoPacket> infoPackets) {
        ArrayList<InfoPacket> processedList = new ArrayList<>();
        processedList.add(new InfoPacket(new SimpleDateFormat("dd'/'MM'/'yyyy HH:mm:ss").format(new Date()),latitude,longitude,getAddress(latitude,longitude),operator,strength,level));
        //processedList.add(infoPackets.get(0));
        return processedList;
    }



    private void sendLocation() {
        String lat = String.valueOf(latitude);
        String lon = String.valueOf(longitude);
        Log.d(TAG, "sendLocation method : lattitude " + lat + " long " + lon);
        String date_time = new SimpleDateFormat("dd'/'MM'/'yyyy HH:mm:ss").format(new Date());

        Log.d(TAG, "sendLocation: new");


        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CellInfoLte cellinfoLte = null;
        int signal_str = 0;
        int levelInAsu = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cellinfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
            CellSignalStrengthLte cellSignalStrengthLte = cellinfoLte.getCellSignalStrength();
            signal_str = cellSignalStrengthLte.getDbm();
            level = cellSignalStrengthLte.getLevel();
            Log.d(TAG, "sendLocation: "+cellSignalStrengthLte.getDbm());
        }

        //TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<CellInfo> cellInfos = null;   //This will give info of all sims present inside your mobile
            cellInfos = telephonyManager.getAllCellInfo();
            if(cellInfos!=null){
                for (int i = 0 ; i<cellInfos.size(); i++){
                    Log.d(TAG, "Cell Info for : "+i);
                    if (cellInfos.get(i).isRegistered()){
                        Log.d(TAG, i+ " is registered also");
                        if(cellInfos.get(i) instanceof CellInfoWcdma){
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(i);
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                            level = cellSignalStrengthWcdma.getLevel();
                            strength = cellSignalStrengthWcdma.getDbm();
                            levelInAsu = cellSignalStrengthWcdma.getAsuLevel();
                            if(i==0)
                                Toast.makeText(context, "WCDMA", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "sendLocation: cellinfo "+i+" detected in WCDMA");
                        }else if(cellInfos.get(i) instanceof CellInfoGsm){
                            CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(i);
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                            level = cellSignalStrengthGsm.getLevel();
                            strength = cellSignalStrengthGsm.getDbm();
                            levelInAsu = cellSignalStrengthGsm.getAsuLevel();
                            if(i==0)
                                Toast.makeText(context, "GSM", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "sendLocation: cellinfo "+i+" detected in GSM");
                        }else if(cellInfos.get(i) instanceof CellInfoLte){
                            CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(i);
                            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                            level = cellSignalStrengthLte.getLevel();
                            strength = cellSignalStrengthLte.getDbm();
                            levelInAsu = cellSignalStrengthLte.getAsuLevel();
                            if(i==0)
                                Toast.makeText(context, "LTE", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "sendLocation: cellinfo "+i+" detected in LTE");
                        }
                        Log.d(TAG, "For CellInfo: : "+i+" Signal Strength : "+strength+ " Signal Level : "+level+ " Signal ASU Level : "+levelInAsu);
                    }
                }
            }
        }
        Log.d(TAG, "sendLocation: Before Calculation");
        Log.d(TAG, "sendLocation: "+latitude);
        int lat_deg = (int)latitude;
        int lat_min = (int)((latitude % 1)*60);
        int lon_deg = (int)longitude;
        int lon_min = (int)((longitude % 1)*60);

        Log.d(TAG, "sendLocation: userEmail "+ userEmail);

        String date = new SimpleDateFormat("yyyy'/'MM'/'dd").format(new Date());

        Log.d(TAG, "sendLocation: generating packet");

        InfoPacket infoPacket = new InfoPacket(date,latitude,longitude,getAddress(latitude,longitude),operator,signal_str,level);
        /*DatabaseReference packetAddress = rootRef
                .child(getApplicationContext().getString(R.string.firebase_raw))
                .child(String.valueOf(lat_deg))
                .child(String.valueOf(lon_deg))
                .child(String.valueOf(lat_min))
                .child(String.valueOf(lon_min))
                .child(operator)
                .child(date)
                .child(userEmail);
        packetAddress.setValue(infoPacket);*/
        if(infoPacket.getLevel()<2) {
            Log.d(TAG, "sendLocation: Coverage < 2");
            if (database.insertCrashReport(infoPacket) > 0)
                Log.d(TAG, "storeCrashReport: insert Successful");
            else
                Log.d(TAG, "storeNormalReport: insert Failed");
        }else {
            Log.d(TAG, "sendLocation: Coverage >= 2");
            if (database.insertNormalReport(infoPacket) > 0)
                Log.d(TAG, "storeNormalReport: insert Successful");
            else
                Log.d(TAG, "storeNormalReport: insert Failed");
        }
    }

    public String getAddress(double lat, double lng) {
        String add = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            /*add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();
            add= add + "\n" + obj.getSubLocality();
            add= add + "\n" + obj.getPremises();*/
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return add;
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(context, "Enable Network and GPS", Toast.LENGTH_SHORT).show();
            } else {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(context, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }


    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(NetworkService.this);
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS Error");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("", "In onDestroy");
        Toast.makeText(this, "Service Detroyed!", Toast.LENGTH_SHORT).show();
    }

}
