package com.itonlab.kitcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.model.MenuItem;

import java.util.ArrayList;

public class DatabaseListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<MenuItem> menuItems;

    public DatabaseListAdapter(Context mContext, ArrayList<MenuItem> menuItems) {
        this.mContext = mContext;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return menuItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.database_list_item,parent, false);
        }

        MenuItem menuItem = menuItems.get(position);
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        tvId.setText(String.valueOf(menuItem.getId()));
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        tvName.setText(menuItem.getNameThai());
        TextView tvPrice  = (TextView) convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(String.valueOf(menuItem.getPrice()));

        return convertView;
    }
}
