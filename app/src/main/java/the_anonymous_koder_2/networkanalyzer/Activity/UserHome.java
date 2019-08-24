package the_anonymous_koder_2.networkanalyzer.Activity;


        import android.annotation.SuppressLint;
        import android.app.AlarmManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.support.annotation.NonNull;
        import android.support.design.widget.BottomNavigationView;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.Switch;
        import android.widget.Toast;

        import com.google.android.gms.auth.api.signin.GoogleSignIn;
        import com.google.android.gms.auth.api.signin.GoogleSignInClient;
        import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;

        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;

        import the_anonymous_koder_2.networkanalyzer.CrashReportsFragment;
        import the_anonymous_koder_2.networkanalyzer.Database.Database;
        import the_anonymous_koder_2.networkanalyzer.HomeFragment;
        import the_anonymous_koder_2.networkanalyzer.LogFragment;
        import the_anonymous_koder_2.networkanalyzer.Model.EventLog;
        import the_anonymous_koder_2.networkanalyzer.Model.InfoPacket;
        import the_anonymous_koder_2.networkanalyzer.NormalReportsFragment;
        import the_anonymous_koder_2.networkanalyzer.OperatorFragment;
        import the_anonymous_koder_2.networkanalyzer.Preferences;
        import the_anonymous_koder_2.networkanalyzer.R;
        import the_anonymous_koder_2.networkanalyzer.Service.DataPushService;

        import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
        import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class UserHome extends AppCompatActivity {

    Context context;
    FirebaseAuth auth;
    String TAG = "User Home";
    Switch aSwitch;
    Database db;
    final static int GPS_PERMISSION_REQ = 124;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        context = this;
        db = new Database();
        BottomNavigationView navigationView = findViewById(R.id.home_nav);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.home_bn_home:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                    case R.id.home_bn_operators:
                        selectedFragment = OperatorFragment.newInstance();
                        break;
                    case R.id.home_bn_crash:
                        selectedFragment = CrashReportsFragment.newInstance();
                        break;
                    case R.id.home_bn_log:
                        selectedFragment = LogFragment.newInstance();
                        break;
                    case R.id.home_bn_normal:
                        selectedFragment = NormalReportsFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.home_frame, selectedFragment);
                transaction.commit();
                return true;
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame,HomeFragment.newInstance());
        transaction.commit();
        if(!checkGPSPermission()){
                reqGPSPermission();
            }
            //
        insertIntoDb();
  //      Calendar calendar = Calendar.getInstance();
        /*AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long crashPushInterval = 1000 * 60 * 1; // 5 minutes in milliseconds
        long normalPushInterval = 1000 * 60 * 1; // 5 minutes in milliseconds
        Intent crashReportIntent = new Intent(context, DataPushService.class);
        crashReportIntent.putExtra(DataPushService.REQ_TYPE,DataPushService.PUSH_CRASH_REQ);
        Intent normalReportIntent = new Intent(context, DataPushService.class);
        normalReportIntent.putExtra(DataPushService.REQ_TYPE,DataPushService.PUSH_NORMAL_REQ);
        PendingIntent crashPendingIntent =
                PendingIntent.getService(context,
                        DataPushService.SERVICE_ID_C,
                        crashReportIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent normalPendingIntent =
                PendingIntent.getService(context,
                        DataPushService.SERVICE_ID_N,
                        normalReportIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                crashPushInterval,
                crashPendingIntent
        );
        am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                normalPushInterval,
                normalPendingIntent
        );*/
    }

    private void insertIntoDb() {
        for (int i = 0; i < 5; i++) {
            db.insertCrashReport(new InfoPacket(new SimpleDateFormat("dd'/'MM'/'yyyy HH:mm:ss").format(new Date()),29.5421,77.5487,"Area","Jio 4G",-64,0));
            //db.insertNormalReport(new InfoPacket("26-03-2018",27.33,78.1,"Bikaner","Jio 4G",-87,1));
            db.addLog(new EventLog("Data Uploaded","DB Updated",new SimpleDateFormat("dd'/'MM'/'yyyy HH:mm:ss").format(new Date())));
        }
    }

    @SuppressLint("RestrictedApi")
    private void signOut(){
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
        signInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "onComplete: signed Out successfully");
                    Preferences.saveLoggedIn(context,false);
                    startActivity(new Intent(context,Authenticate.class));
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    /*private void preProcess() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        Log.d(TAG, "onClick: " + telephonyManager.getNetworkOperatorName());
        .putExtra("OPERATOR", telephonyManager.getNetworkOperatorName());
    }*/

    public boolean checkGPSPermission(){
        int gpsResult1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int gpsResult2 = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        return gpsResult1 == PackageManager.PERMISSION_GRANTED && gpsResult2 == PackageManager.PERMISSION_GRANTED;
    }

    public void reqGPSPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},GPS_PERMISSION_REQ);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case GPS_PERMISSION_REQ:
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
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
}
