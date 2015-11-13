package com.itonlab.kitcher.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itonlab.kitcher.R;
import com.itonlab.kitcher.database.KitcherDao;
import com.itonlab.kitcher.model.MenuItem;
import com.itonlab.kitcher.model.Picture;

import java.util.ArrayList;

public class FoodListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<MenuItem> menuItems;

    static class ViewHolder {
        TextView tvName = null;
        TextView tvPrice = null;
        ImageView ivImgFood = null;
    }

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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.food_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            viewHolder.ivImgFood = (ImageView) convertView.findViewById(R.id.ivImgFood);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            LoadPictureTask reuseTask = (LoadPictureTask) viewHolder.ivImgFood.getTag();
            if (reuseTask != null) {
                reuseTask.cancel(true);
            }
        }

        MenuItem menuItem = menuItems.get(position);
        viewHolder.tvName.setText(menuItem.getNameThai());
        viewHolder.tvPrice.setText(Double.toString(menuItem.getPrice()));


        KitcherDao databaseDao = new KitcherDao(mContext);
        databaseDao.open();
        Picture picture = databaseDao.getMenuPicture(menuItem.getPictureId());
        databaseDao.close();

        LoadPictureTask loadPictureTask = new LoadPictureTask(viewHolder.ivImgFood, picture);
        loadPictureTask.execute();
        viewHolder.ivImgFood.setTag(loadPictureTask);

        return convertView;
    }

    class LoadPictureTask extends AsyncTask<Void, Void, Bitmap> {
        ImageView imageView;
        Picture picture;

        public LoadPictureTask(ImageView imageView, Picture picture) {
            this.imageView = imageView;
            this.picture = picture;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return picture.getBitmapPicture();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }

    }

}
