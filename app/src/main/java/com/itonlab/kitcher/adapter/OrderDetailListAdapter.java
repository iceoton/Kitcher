package com.itonlab.kitcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.model.OrderDetailItem;

import java.util.ArrayList;

public class OrderDetailListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<OrderDetailItem> orderDetailItems;

    public OrderDetailListAdapter(Context mContext, ArrayList<OrderDetailItem> orderDetailItems) {
        this.mContext = mContext;
        this.orderDetailItems = orderDetailItems;
    }

    @Override
    public int getCount() {
        return orderDetailItems.size();
    }

    @Override
    public Object getItem(int position) {
        return orderDetailItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.bill_list_item, parent, false);
        }

        OrderDetailItem summaryItem = orderDetailItems.get(position);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        tvName.setText(summaryItem.getName());
        TextView tvNumber = (TextView)convertView.findViewById(R.id.tvNumber);
        tvNumber.setText(summaryItem.getAmount()+ "x");
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(summaryItem.getPrice()));
        TextView tvTotalPrice = (TextView)convertView.findViewById(R.id.tvTotalPrice);
        double totalPrice = summaryItem.getPrice() * summaryItem.getAmount();
        tvTotalPrice.setText(Double.toString(totalPrice));

        return convertView;
    }
}
