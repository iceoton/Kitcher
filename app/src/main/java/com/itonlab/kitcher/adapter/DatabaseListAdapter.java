package com.itonlab.kitcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.model.FoodItem;

import java.util.ArrayList;

public class DatabaseListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<FoodItem> foodItems;

    public DatabaseListAdapter(Context mContext, ArrayList<FoodItem> foodItems) {
        this.mContext = mContext;
        this.foodItems = foodItems;
    }

    @Override
    public int getCount() {
        return foodItems.size();
    }

    @Override
    public Object getItem(int position) {
        return foodItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return foodItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.database_list_item,parent, false);
        }

        FoodItem foodItem = foodItems.get(position);
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        tvId.setText(String.valueOf(foodItem.getId()));
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        tvName.setText(foodItem.getNameThai());
        TextView tvPrice  = (TextView) convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(String.valueOf(foodItem.getPrice()));

        return convertView;
    }
}
