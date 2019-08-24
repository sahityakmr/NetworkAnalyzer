package the_anonymous_koder_2.networkanalyzer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import the_anonymous_koder_2.networkanalyzer.Model.User;

/**
 * Created by Sahitya on 3/21/2018.
 */

public class Preferences {
    private static final String IS_LOGGED_IN_KEY = "isloggedin";
    private static SharedPreferences sp;
    private static SharedPreferences.Editor ed;
    final static private String preferences = "NetworkAnalyzer";
    final static private String USER_KEY = "user";

    private static void read(Context context){
        sp = context.getSharedPreferences(preferences,Context.MODE_PRIVATE);
    }

    private static void readWrite(Context context){
        sp = context.getSharedPreferences(preferences,Context.MODE_PRIVATE);
        ed = sp.edit();
    }

    private static void stopReadWrite(){
        if(ed!=null)
            ed.commit();
        sp = null;
        ed = null;
    }

    public static void saveUser(Context context,User user){
        readWrite(context);
        ed.putString(USER_KEY,new Gson().toJson(user));
        stopReadWrite();
    }

    public static User getUser(Context context){
        read(context);
        User user = new Gson().fromJson(sp.getString(USER_KEY,""),User.class);
        stopReadWrite();
        return user;
    }

    public static void saveLoggedIn(Context context,boolean value){
        readWrite(context);
        ed.putBoolean(IS_LOGGED_IN_KEY,value);
        stopReadWrite();
    }

    public static boolean getLoggedIn(Context context){
        boolean isLoggedIn = false;
        read(context);
        isLoggedIn = sp.getBoolean(IS_LOGGED_IN_KEY,false);
        stopReadWrite();
        return isLoggedIn;
    }
}
