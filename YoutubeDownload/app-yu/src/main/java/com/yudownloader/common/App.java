package com.yudownloader.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.yudownloader.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;



/**
 * Created by Avinash Kahal on 02-Dec-17.
 */

public class App extends Application {

    public static String TAG = "APP";
    //public static String strFolderYoutube = "Youtube-Download";
    public static String strFolderYoutube = "Avinash Kahar";

    public static String strDicFullMain = Environment.getExternalStorageDirectory() + File.separator + App.strFolderYoutube;


    public static Context context;


    public static String strGoogleKey = "AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8";
    public static int strTotalVideo = 10;


    //https://www.googleapis.com/youtube/v3/search?key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=&part=snippet,id&order=date&maxResults=50
    public static String strBaseURL = "https://www.googleapis.com/youtube/v3/";



    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        create_FolderYoutube();


        /*------ Facebook image loading ---------*/
        Fresco.initialize(this);
    }

    public static void create_FolderYoutube() {
        FileOutputStream out = null;
        try {
            String directoryPath = Environment.getExternalStorageDirectory() + File.separator + App.strFolderYoutube;
            File appDir = new File(directoryPath);
            if (!appDir.exists() && !appDir.isDirectory()) {
                if (appDir.mkdirs()) {
                    App.showLog("===CreateDir===", "App dir created");
                } else {
                    App.showLog("===CreateDir===", "Unable to create app dir!");
                }
            } else {
                //App.showLog("===CreateDir===","App dir already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_theme);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.whiteTR100));
            window.setNavigationBarColor(activity.getResources().getColor(R.color.whiteTR100));

            window.setBackgroundDrawable(background);
        }
    }

    public static void showLog(String From, String msg) {
        //Toast.makeText(context, From+" : "+msg, Toast.LENGTH_SHORT).show();
        System.out.println("From: " + From + " :---: " + msg);
    }

    public static void showToast(String msg) {
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();

    }


    /*
    * Seconds to Time
    * */
    public static String timeConversion(String totalSeconds) {

        int intTotalSeconds = Integer.parseInt(totalSeconds);
        int MINUTES_IN_AN_HOUR = 60;
        int SECONDS_IN_A_MINUTE = 60;

        int seconds = intTotalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = intTotalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        String totalTime = "";
        if(hours > 0)
        {
            totalTime = hours + ":" + minutes + ":" + seconds;
        }
        else
        {
            totalTime = minutes + ":" + seconds;
        }

        return totalTime;
    }


    /*
    * Get extenstion  -- https://stackoverflow.com/questions/9758151/get-the-file-extension-from-images-picked-from-gallery-or-camera-as-string
    * */
    public static String getExtention(String strUrl)
    {
        return strUrl.substring(strUrl.lastIndexOf("."));    // Extension with dot .jpg, .png
        //return strUrl.substring(strUrl.lastIndexOf(".") + 1);   // Without dot jpg, png
    }


    /*------ Check Internet -------*/
    public static boolean isInternetAvail(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }


}
