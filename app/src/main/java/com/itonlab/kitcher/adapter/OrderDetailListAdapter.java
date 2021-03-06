package com.itonlab.kitcher.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.model.OrderItem;
import com.itonlab.kitcher.model.OrderItemDetail;

import java.util.ArrayList;

public class OrderDetailListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<OrderItemDetail> orderItemDetails;

    public OrderDetailListAdapter(Context mContext, ArrayList<OrderItemDetail> orderItemDetails) {
        this.mContext = mContext;
        this.orderItemDetails = orderItemDetails;
    }

    @Override
    public int getCount() {
        return orderItemDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return orderItemDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.order_item_list_item, parent, false);
        }

        OrderItemDetail orderItemDetail = orderItemDetails.get(position);

        FrameLayout layoutOrderItemListItem = (FrameLayout)
                convertView.findViewById(R.id.layoutOrderItemListItem);
        TextView txtOrderItemStatus = (TextView) convertView.findViewById(R.id.txtItemStatus);

        if (orderItemDetail.getStatus().equals(OrderItem.Status.DONE)) {
            Drawable bgDrawable = mContext.getResources().getDrawable(R.drawable.bg_stroke_yellow);
            String strStatus = mContext.getResources().getString(R.string.order_item_status_done);
            int statusColor = mContext.getResources().getColor(R.color.yellow_shadow);

            if (orderItemDetail.isServed()) {
                convertView.setEnabled(false);
                bgDrawable = mContext.getResources().getDrawable(R.drawable.bg_stroke_red);
                strStatus = mContext.getResources().getString(R.string.order_item_status_served);
                statusColor = mContext.getResources().getColor(R.color.red);
            }

            txtOrderItemStatus.setVisibility(View.VISIBLE);
            txtOrderItemStatus.setText(strStatus);
            txtOrderItemStatus.setTextColor(statusColor);
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                layoutOrderItemListItem.setBackground(bgDrawable);
            } else {
                layoutOrderItemListItem.setBackgroundDrawable(bgDrawable);
            }
        }

        OrderItemDetail orderDetailItem = orderItemDetails.get(position);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        tvName.setText(orderDetailItem.getName());
        TextView tvNumber = (TextView)convertView.findViewById(R.id.tvNumber);
        tvNumber.setText(orderDetailItem.getQuantity() + "รายการx");
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(orderDetailItem.getPrice()) + "บาท");
        TextView tvTotalPrice = (TextView)convertView.findViewById(R.id.tvTotalPrice);
        double totalPrice = orderDetailItem.getPrice() * orderDetailItem.getQuantity();
        tvTotalPrice.setText(Double.toString(totalPrice) + "บาท");
        TextView tvOption = (TextView) convertView.findViewById(R.id.textViewOption);
        tvOption.setText(orderDetailItem.getOption());

        return convertView;
    }
}
