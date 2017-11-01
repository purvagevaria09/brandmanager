package com.pg.brandtest.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pg.brandtest.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Purva Gevaria
 * Class name : NetworkReceiver
 * Functionality : Receiver for detecting internet
 */

public class NetworkReceiver extends BroadcastReceiver {
    private String TAG = "NetworkReceiver";
    private Context context;
    private DatabaseHelper databaseHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                postUnsyncedData();

            } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {

            }
        }
    }


    /**
     * will post unsynced data
     */
    private void postUnsyncedData() {
        Cursor cursor = new DatabaseHelper(context).getUnSyncedData();
        JSONObject jsonObject = new JSONObject();

        if (cursor != null) {
            cursor.moveToFirst();
            JSONArray brand = new JSONArray();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                try {
                    JSONObject node = new JSONObject();
                    String id = cursor.getString(0);
                    node.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    node.put("description", cursor.getString(cursor.getColumnIndex("description")));
                    brand.put(node);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            try {
                jsonObject.put("brand", brand);


                calLWebserviceAndPostData(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }   //end of postUnsyncedData


    /***
     * will call webservice and post data
     */
    private void calLWebserviceAndPostData(JSONObject jsonObject) {
        String BASE_URL = "http://18.221.239.118/test/";
        String POST_URL = BASE_URL + "insert.php";

        final HashMap<String, String> params = new HashMap<>();
        params.put("data", jsonObject.toString());

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {

                            try {

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };


        RetryPolicy policy = new DefaultRetryPolicy(15000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }   //end of postInsertedData

}   //end of calLWebserviceAndPostData



