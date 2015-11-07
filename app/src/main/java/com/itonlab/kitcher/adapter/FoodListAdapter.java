package com.itonlab.kitcher.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.model.MenuItem;
import com.itonlab.kitcher.util.FileManager;

import java.util.ArrayList;

public class FoodListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<MenuItem> menuItems;

    public FoodListAdapter(Context context, ArrayList<MenuItem> menuItems) {
        this.mContext = context;
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
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.food_list_item, parent, false);
        }
        MenuItem menuItem = menuItems.get(position);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        tvName.setText(menuItem.getNameThai());
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(menuItem.getPrice()));
        ImageView ivImgFood = (ImageView)convertView.findViewById(R.id.ivImgFood);
        FileManager fileManager = new FileManager(mContext);
        Drawable drawable = fileManager.getDrawableFromAsset(menuItem.getImgPath());
        ivImgFood.setImageDrawable(drawable);

        return convertView;
    }

}
