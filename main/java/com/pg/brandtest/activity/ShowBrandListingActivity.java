package com.pg.brandtest.activity;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pg.brandtest.R;
import com.pg.brandtest.adapter.BrandListAdapter;
import com.pg.brandtest.database.DatabaseHelper;
import com.pg.brandtest.model.BrandModel;
import com.pg.brandtest.util.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * Created by Purva Gevaria
 * Class name : ShowBrandListingActivity
 * Functionality : Will show the brand list from webservice and user can add brand and description
 */
public class ShowBrandListingActivity extends AppCompatActivity implements BrandListAdapter.DataTransferInterface, View.OnClickListener {

    private String TAG = "ShowBrandListingActivity";

    private RecyclerView listBrand;
    private ProgressBar progress;
    private List<BrandModel> listData;

    private FloatingActionButton fab;

    private RequestQueue mRequestQueue;
    private DatabaseHelper databaseHandler;

    private final String BASE_URL = "http://18.221.239.118/test/";
    private String brandName = "", brandDescription = "", brandCreatedAt = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_brand_listing);

        initObjects();

        initUIControls();

        registerForListener();

        callGetBranchDataWebservice();

        postUnSyncedData();

    }


    private void initObjects() {
        databaseHandler = new DatabaseHelper(this);
    }

    /**
     * will call get brand webservice
     */
    private void callGetBranchDataWebservice() {
        String POST_URL = BASE_URL + "list.php";
        if (NetworkUtil.isNetworkAvailable(this)) {
            progress.setVisibility(View.VISIBLE);

            mRequestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (isStrNotNull(response)) {
                                Log.d(TAG, "Response :: " + response);
                                try {
                                    JSONObject responseObject = new JSONObject(response);
                                    if (responseObject.optString("error_code").equalsIgnoreCase("1")) {
                                        parseDataAndBindRecyclerView(responseObject.optJSONArray("brand_list"));

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    progress.setVisibility(View.GONE);
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();

                        }
                    });

            RetryPolicy policy = new DefaultRetryPolicy(15000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            mRequestQueue.add(stringRequest);
        } else {
            Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
        }
    }   //end of callGetBranchDataWebservice


    /**
     * will parse response brand array and bind recylerview
     *
     * @param brand_list
     */
    private void parseDataAndBindRecyclerView(JSONArray brand_list) {
        if (brand_list != null && brand_list.length() > 0) {
            listData = new ArrayList<>();
            for (int i = 0; i < brand_list.length(); i++) {         //itrate brand array
                JSONObject brandObject = brand_list.optJSONObject(i);
                if (brandObject != null) {
                    BrandModel brandModel = new BrandModel();
                    brandModel.setId(brandObject.optString("id"));
                    brandModel.setName(brandObject.optString("name"));
                    brandModel.setDescription(brandObject.optString("description"));
                    brandModel.setCreatedAt(brandObject.optString("created_at"));
                    brandModel.setIsSynced("true");    //default set false
                    databaseHandler.addBrand(brandModel);
                    listData.add(brandModel);
                }
            }
            if (listData.size() > 0) {
                setAdapter();
            }
        }
    }   //end of parseDataAndBindRecylerView


    //initialization of ui controls
    private void initUIControls() {
        progress = (ProgressBar) findViewById(R.id.progress);
        listBrand = (RecyclerView) findViewById(R.id.listBrand);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }


    //register components for click listener
    private void registerForListener() {
        fab.setOnClickListener(this);
    }


    //set list adapter
    private void setAdapter() {
        listBrand.setAdapter(new BrandListAdapter(this, listData, this));
        listBrand.setLayoutManager(new LinearLayoutManager(this));
    }


    /**
     * it will take input as a string and check wether it is not null and  not empty and return true or false
     */

    public boolean isStrNotNull(String string) {
        return string != null && !string.isEmpty();
    }   //end of isStrNotNull()


    public void showPopUp() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_insert_brand);
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        final TextView txtBrandName = (TextView) dialog.findViewById(R.id.txtBrandName);
        final TextView txtDescription = (TextView) dialog.findViewById(R.id.txtDescription);
        final Button btnInsert = (Button) dialog.findViewById(R.id.btnInsert);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStrNotNull(txtBrandName.getText().toString())) {
                    brandName = txtBrandName.getText().toString();

                } else {
                    Toast.makeText(ShowBrandListingActivity.this, "Please enter brand name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isStrNotNull(txtDescription.getText().toString())) {
                    brandDescription = txtDescription.getText().toString();

                } else {
                    Toast.makeText(ShowBrandListingActivity.this, "Please enter brannd description", Toast.LENGTH_SHORT).show();
                    return;
                }
                insertDataIntoDb(brandName, brandDescription, brandCreatedAt);
                dialog.dismiss();
            }
        });

    }

    /***
     * will insert brand data into db
     * @param brandName
     * @param brandDescription
     * @param brandCreatedAt
     */
    private void insertDataIntoDb(String brandName, String brandDescription, String brandCreatedAt) {
        BrandModel brandModel = new BrandModel();
        if (NetworkUtil.isNetworkAvailable(this)) {
            brandModel.setIsSynced("true");
        } else {
            brandModel.setIsSynced("false");
        }
        brandModel.setName(brandName);
        brandModel.setDescription(brandDescription);
        brandModel.setCreatedAt(getCurrentDate());
        databaseHandler.addBrand(brandModel);

        postInsertedData(brandName, brandDescription, brandCreatedAt);
        listData.clear();
        listData = databaseHandler.getAllBrand();
        setAdapter();

    }   //end of insertDataIntoDb


    /**
     * will post inserted data
     *
     * @param brandName
     * @param brandDescription
     * @param brandCreatedAt
     */
    private void postInsertedData(String brandName, String brandDescription, String brandCreatedAt) {
        String POST_URL = BASE_URL + "insert.php";
        final HashMap<String, String> params = new HashMap<>();
        params.put("data", createJsonData(brandName, brandDescription));
        if (NetworkUtil.isNetworkAvailable(this)) {
            progress.setVisibility(View.VISIBLE);

            mRequestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (isStrNotNull(response)) {
                                Log.d(TAG, "Response :: " + response);
                                try {
                                    JSONObject responseObject = new JSONObject(response);
                                    String error_code = responseObject.optString("error_code");
                                    if (error_code.equalsIgnoreCase("1")) {
                                        clearTableData();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    progress.setVisibility(View.GONE);
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            RetryPolicy policy = new DefaultRetryPolicy(15000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            mRequestQueue.add(stringRequest);

        } else {
            Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
        }
    }   //end of postInsertedData


    /**
     * will clear table data
     */
    private void clearTableData() {
        databaseHandler.truncateTable();
        postUnSyncedData();
    }   //end of clearTableData


    /**
     * will creat json data
     *
     * @return
     */
    private String createJsonData(String brandName, String brandDescription) {
        JSONObject jsonObject = new JSONObject();
        JSONArray brand = new JSONArray();
        JSONObject node = new JSONObject();
        try {
            node.put("brand", brandName);
            node.put("description", brandDescription);

            brand.put(node);
            jsonObject.put("brand", brand);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null)
            return jsonObject.toString();
        else
            return null;
    }   //end of createJsonData


    /**
     * will give current date`
     *
     * @return
     */
    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        return c + "";

    }   //end of convertTimeInAmPn

    /**
     * will post unsynced data
     */
    private void postUnSyncedData() {
        Cursor cursor = new DatabaseHelper(this).getUnSyncedData();
        JSONObject jsonObject = new JSONObject();
        if (cursor != null) {
            cursor.moveToFirst();
            JSONArray brand = new JSONArray();
            for (int i = 0; i < cursor.getCount(); i++) {
                try {
                    JSONObject node = new JSONObject();
                    node.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    node.put("description", cursor.getString(cursor.getColumnIndex("description")));
                    brand.put(node);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }
            try {
                jsonObject.put("brand", brand);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }   //end of postUnSyncedData

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                showPopUp();
                break;
        }
    }


}   //end of ShowBrandListingActivity



