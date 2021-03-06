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

public class HistoryDetailListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Order> orderItems;

    public HistoryDetailListAdapter(Context mContext, ArrayList<Order> orderItems) {
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
        return orderItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.history_detail_item, parent, false);
        }

        Order order = orderItems.get(position);
        TextView tvOrderNumber = (TextView)convertView.findViewById(R.id.tvOrderNumber);
        tvOrderNumber.setText(String.valueOf(position + 1)); // ที่บวก 1 เพราะมันเริ่มนับจาก 0 สะดวกในการมองของผู้ใช้
        TextView tvCustomerName = (TextView) convertView.findViewById(R.id.tvCustomerName);
        tvCustomerName.setText(order.getCustomerName());
        TextView tvTake = (TextView) convertView.findViewById(R.id.textViewTake);
        if (order.getTake().equals(Order.Take.HOME)) {
            tvTake.setText(R.string.text_take_home);
        } else {
            tvTake.setText(R.string.text_take_here);
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
