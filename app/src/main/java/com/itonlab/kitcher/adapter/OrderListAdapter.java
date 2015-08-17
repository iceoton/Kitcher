package com.itonlab.kitcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.model.FoodOrder;

import java.util.ArrayList;

public class OrderListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<FoodOrder> orderItems;

    public OrderListAdapter(Context mContext, ArrayList<FoodOrder> orderItems) {
        this.mContext = mContext;
        this.orderItems = orderItems;
    }

    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public Object getItem(int position) {
        return orderItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.order_list_item, parent, false);
        }

        FoodOrder order = orderItems.get(position);
        TextView tvCustomerName = (TextView)convertView.findViewById(R.id.tvCustomerName);
        tvCustomerName.setText(order.getCustomerName());
        TextView tvOrderTime = (TextView)convertView.findViewById(R.id.tvOrderTime);
        tvOrderTime.setText(order.getOrderTime().toString());

        return convertView;
    }
}
