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
import com.itonlab.kitcher.model.FoodItem;
import com.itonlab.kitcher.util.FileManager;

import java.util.ArrayList;

public class FoodListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<FoodItem>  foodItems;

    public FoodListAdapter(Context context, ArrayList<FoodItem> foodItems){
        this.mContext = context;
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
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.food_list_item, parent, false);
        }
        FoodItem foodItem = foodItems.get(position);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        tvName.setText(foodItem.getNameThai());
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(foodItem.getPrice()));
        ImageView ivImgFood = (ImageView)convertView.findViewById(R.id.ivImgFood);
        FileManager fileManager = new FileManager(mContext);
        Drawable drawable = fileManager.getDrawableFromAsset(foodItem.getImgPath());
        ivImgFood.setImageDrawable(drawable);

        return convertView;
    }

}
