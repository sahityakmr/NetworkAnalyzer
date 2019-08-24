package the_anonymous_koder_2.networkanalyzer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.FirebaseDatabase;

import the_anonymous_koder_2.networkanalyzer.Service.NetworkService;

/**
 * Created by Sahitya on 3/21/2018.
 */

public class AppController extends Application {
    public static Context context;
    String TAG = "App Controller";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        context = getApplicationContext();
        Fresco.initialize(this);
        ComponentName serviceOne = new ComponentName(context, NetworkService.class);
        ComponentName serviceTwo = new ComponentName(context, NetworkService.class);
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(serviceOne,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, // or  COMPONENT_ENABLED_STATE_DISABLED
                PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(serviceTwo,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, // or  COMPONENT_ENABLED_STATE_DISABLED
                PackageManager.DONT_KILL_APP);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
