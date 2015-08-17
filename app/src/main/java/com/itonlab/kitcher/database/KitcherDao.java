package com.itonlab.kitcher.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itonlab.kitcher.model.FoodItem;
import com.itonlab.kitcher.model.FoodOrder;
import com.itonlab.kitcher.model.FoodOrderItem;
import com.itonlab.kitcher.model.OrderItemTable;
import com.itonlab.kitcher.model.OrderTable;

import java.util.ArrayList;

public class KitcherDao {
    private static final String TAG = "DATABASE";
    private Context mContext;
    private SQLiteDatabase database;
    private KitcherOpenHelper openHelper;

    public KitcherDao(Context context) {
        this.mContext = context;
        openHelper = new KitcherOpenHelper(context);
    }

    public void open() throws SQLException {
        database = openHelper.getWritableDatabase();
    }

    public void close() {
        openHelper.close();
    }

    public ArrayList<FoodItem> getMenu(){
        ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();
        String sql = "SELECT * FROM menu";
        Cursor cursor = database.rawQuery(sql,null);

        if(cursor.getCount() > 0){
            FoodItem foodItem = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                foodItem = FoodItem.newInstance(cursor);
                foodItems.add(foodItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of food in menu: " + foodItems.size());

        return  foodItems;
    }

    public void addOrder(FoodOrder foodOrder){
        ContentValues values = foodOrder.toContentValues();
        long insertIndex = database.insert(OrderTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order table.");
        } else {
            Log.d(TAG, "insert order successful.");
        }
    }

    public void addOrderItem(FoodOrderItem foodOrderItem){
        ContentValues values = foodOrderItem.toContentValues();
        long insertIndex = database.insert(OrderItemTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order_item table.");
        } else {
            Log.d(TAG, "insert order_item successful.");
        }
    }

    public ArrayList<FoodOrder> getAllOrder(){
        ArrayList<FoodOrder> foodOrders = new ArrayList<FoodOrder>();
        String sql = "SELECT * FROM order";
        Cursor cursor = database.rawQuery(sql,null);

        if(cursor.getCount() > 0){
            FoodOrder foodOrder = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                foodOrder = foodOrder.newInstance(cursor);
                foodOrders.add(foodOrder);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of order: " + foodOrders.size());

        return foodOrders;
    }

}
