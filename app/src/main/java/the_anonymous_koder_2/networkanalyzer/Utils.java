package the_anonymous_koder_2.networkanalyzer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import the_anonymous_koder_2.networkanalyzer.Interface.AsyncResponse;

import static android.content.ContentValues.TAG;

/**
 * Created by Sahitya on 3/21/2018.
 */

public class Utils {
    public static String getEncodedEmail(String email){
        email = email.replaceAll("@","(at)");
        email = email.replaceAll("\\.","(dot)");
        return email;
    }

    public static String getDecodedEmail(String email){
        email = email.replaceAll("(at)","@");
        email = email.replaceAll("(dot)",".");
        return email;
    }

    public static boolean hasActiveInternetConnection(Context context, AsyncResponse response) {
        if (isNetworkAvailable(context)) {
            new MyDownloadTask(response).execute();
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    static class MyDownloadTask extends AsyncTask<Void,Void,Boolean>
    {
        AsyncResponse response;

        public MyDownloadTask(AsyncResponse response) {
            this.response = response;
        }

        protected void onPreExecute() {
        //display progress dialog.
        }
        protected Boolean doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL("http://www.google.com");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                if(con.getResponseCode() == 200)
                    response.sendBoolean(true);
                else
                    response.sendBoolean(false);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            return;
        }
    }


}
