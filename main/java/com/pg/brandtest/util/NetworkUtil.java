package com.pg.brandtest.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/***
 * Class Name :: NetworkUtil
 **/
public class NetworkUtil {

    /*****
     * this method will check internet connection is available or not
     *
     * @param context
     * @return
     *****/
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }//end of isNetworkAvailable


}//end of NetworkUtil class

