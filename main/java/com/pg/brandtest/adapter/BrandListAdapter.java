package com.pg.brandtest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pg.brandtest.R;
import com.pg.brandtest.model.BrandModel;

import java.util.List;

/**
 * Created by PG on 31/10/17.
 */

public class BrandListAdapter extends RecyclerView.Adapter<BrandListAdapter.BrandHolder> {

    private String TAG = "ActivityAdapter";
    private Context context;
    private List<BrandModel> listData;
    private DataTransferInterface listener;


    public BrandListAdapter(Context context, List<BrandModel> listData, DataTransferInterface listener) {
        this.context = context;
        this.listData = listData;

        this.listener = listener;
        Log.d(TAG, "listData size :: " + listData.size());
    }

    @Override
    public BrandHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(context).inflate(R.layout.single_brand, parent, false);

        // create ViewHolder
        BrandHolder viewHolder = new BrandHolder(itemLayoutView);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(BrandHolder holder, int position) {

        BrandModel brandModel = listData.get(position);
        holder.txtBrandId.setText(brandModel.getId());
        holder.txtBrandName.setText(brandModel.getName());
        holder.txtDescription.setText(brandModel.getDescription());
        holder.txtCreatedAt.setText(brandModel.getCreatedAt());


    }


    @Override
    public int getItemCount() {
        if (listData.size() > 0)
            return listData.size();
        else
            return 0;
    }

    public class BrandHolder extends RecyclerView.ViewHolder {
        TextView txtBrandId, txtBrandName, txtDescription, txtCreatedAt;


        public BrandHolder(View itemView) {
            super(itemView);

            txtBrandId = (TextView) itemView.findViewById(R.id.txtBrandId);
            txtBrandName = (TextView) itemView.findViewById(R.id.txtBrandName);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtCreatedAt = (TextView) itemView.findViewById(R.id.txtCreatedAt);


        }
    }


    public interface DataTransferInterface {
        void showPopUp();
    }
}   //end of class BrandListAdapter

