package com.itonlab.kitcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Order> orderItems;
    int layoutId;

    public OrderListAdapter(Context mContext, ArrayList<Order> orderItems, int layoutId) {
        this.mContext = mContext;
        this.orderItems = orderItems;
        this.layoutId = layoutId;
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
        return orderItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(layoutId, parent, false);
        }

        Order order = orderItems.get(position);
        TextView tvCustomerName = (TextView) convertView.findViewById(R.id.tvCustomerName);
        tvCustomerName.setText(order.getCustomerName());
        if (order.getTake().equals(Order.Take.HOME)) {
            tvCustomerName.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            tvCustomerName.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        TextView tvOrderTime = (TextView) convertView.findViewById(R.id.tvOrderTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.getDefault());
        String dateString = dateFormat.format(order.getOrderTime());
        tvOrderTime.setText(dateString);

        TextView tvNumberOfFood = (TextView)convertView.findViewById(R.id.tvNumberOfFood);
        tvNumberOfFood.setText(String.valueOf(order.getTotalQuantity()));

        return convertView;
    }

}
