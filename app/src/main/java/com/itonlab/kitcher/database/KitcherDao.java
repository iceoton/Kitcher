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
import com.itonlab.kitcher.model.MenuTable;
import com.itonlab.kitcher.model.OrderDetailItem;
import com.itonlab.kitcher.model.OrderItemTable;
import com.itonlab.kitcher.model.OrderTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

    public ArrayList<FoodItem> getMenu() {
        ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();
        String sql = "SELECT * FROM menu";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            FoodItem foodItem = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foodItem = FoodItem.newInstance(cursor);
                foodItems.add(foodItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of food in menu: " + foodItems.size());

        return foodItems;
    }

    public FoodItem getMenuAtId(int menuId) {
        FoodItem foodItem = null;
        String sql = "SELECT * FROM menu WHERE id = ?";
        String[] selectionArgs = {String.valueOf(menuId)};
        Cursor cursor = database.rawQuery(sql, selectionArgs);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            foodItem = FoodItem.newInstance(cursor);
        }

        return foodItem;
    }

    public void updateMenu(FoodItem foodItem) {
        ContentValues values = new ContentValues();
        values.put(MenuTable.Columns._NAME_THAI, foodItem.getNameThai());
        values.put(MenuTable.Columns._PRICE, foodItem.getPrice());
        String[] whereArgs = {String.valueOf(foodItem.getId())};

        int affected = database.update(MenuTable.TABLE_NAME, values, "id=?", whereArgs);
        if (affected == 0) {
            Log.d(TAG, "[Menu]update menu id " + foodItem.getId() + " not successful.");
        }

    }

    public int addOrder(FoodOrder foodOrder) {
        ContentValues values = foodOrder.toContentValues();
        long insertIndex = database.insert(OrderTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order table.");
        } else {
            Log.d(TAG, "insert order successful.");
        }

        return (int) insertIndex;
    }

    public void addOrderItem(FoodOrderItem foodOrderItem) {
        ContentValues values = foodOrderItem.toContentValues();
        long insertIndex = database.insert(OrderItemTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order_item table.");
        } else {
            Log.d(TAG, "insert order_item successful.");
        }
    }

    public ArrayList<FoodOrder> getAllOrder() {
        ArrayList<FoodOrder> foodOrders = new ArrayList<FoodOrder>();
        String sql = "SELECT * FROM 'order'";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            FoodOrder foodOrder = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foodOrder = foodOrder.newInstance(cursor);
                foodOrders.add(foodOrder);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of order: " + foodOrders.size());

        return foodOrders;
    }

    public ArrayList<FoodOrder> getAllOrderNotServed() {
        ArrayList<FoodOrder> foodOrders = new ArrayList<FoodOrder>();
        String sql = "SELECT * FROM 'order' WHERE served=0 ORDER BY order_time DESC";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            FoodOrder foodOrder = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foodOrder = foodOrder.newInstance(cursor);
                foodOrders.add(foodOrder);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of order: " + foodOrders.size());

        return foodOrders;
    }

    public ArrayList<OrderDetailItem> getOrderDetail(int orderId) {
        ArrayList<OrderDetailItem> orderDetailItems = new ArrayList<OrderDetailItem>();
        String sql = "SELECT menu_id, name_th, price, amount" +
                " FROM order_item INNER JOIN menu ON menu_id = menu.id"
                + " WHERE order_id = ?";
        String[] whereArgs = {String.valueOf(orderId)};
        Cursor cursor = database.rawQuery(sql, whereArgs);

        if (cursor.getCount() > 0) {
            OrderDetailItem orderDetailItem = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                orderDetailItem = new OrderDetailItem();
                orderDetailItem.setMenuId(cursor.getInt(0));
                orderDetailItem.setName(cursor.getString(1));
                orderDetailItem.setPrice(cursor.getDouble(2));
                orderDetailItem.setAmount(cursor.getInt(3));
                orderDetailItems.add(orderDetailItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of item in order: " + orderDetailItems.size());

        return orderDetailItems;
    }


    public double getDayIncome(Date date) {
        double income = 0.0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String day = dateFormat.format(date);
        Log.d(TAG, "Get " + day + " income");
        String sql = "SELECT SUM(total_price) as total_price FROM 'order' " +
                "WHERE order_time like '" + day +"%'";
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            income = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
        }
        cursor.close();

        return income;
    }

}
