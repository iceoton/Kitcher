package com.itonlab.kitcher.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itonlab.kitcher.model.MenuItem;
import com.itonlab.kitcher.model.MenuTable;
import com.itonlab.kitcher.model.Order;
import com.itonlab.kitcher.model.OrderDetailItem;
import com.itonlab.kitcher.model.OrderItem;
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

    public ArrayList<MenuItem> getMenu() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        String sql = "SELECT * FROM menu";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            MenuItem menuItem = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                menuItem = MenuItem.newInstance(cursor);
                menuItems.add(menuItem);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of food in menu: " + menuItems.size());

        return menuItems;
    }

    public MenuItem getMenuAtId(int menuId) {
        MenuItem menuItem = null;
        String sql = "SELECT * FROM menu WHERE id = ?";
        String[] selectionArgs = {String.valueOf(menuId)};
        Cursor cursor = database.rawQuery(sql, selectionArgs);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            menuItem = MenuItem.newInstance(cursor);
        }

        return menuItem;
    }

    public void addMenu(MenuItem menuItem) {

    }

    public void deleteMenu(int menuId) {
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(menuId)};
        database.delete(MenuTable.TABLE_NAME, whereClause, whereArgs);
    }

    public void updateMenu(MenuItem menuItem) {
        ContentValues values = new ContentValues();
        values.put(MenuTable.Columns._NAME_THAI, menuItem.getNameThai());
        values.put(MenuTable.Columns._PRICE, menuItem.getPrice());
        String[] whereArgs = {String.valueOf(menuItem.getId())};

        int affected = database.update(MenuTable.TABLE_NAME, values, "id=?", whereArgs);
        if (affected == 0) {
            Log.d(TAG, "[Menu]update menu id " + menuItem.getId() + " not successful.");
        }

    }

    public int addOrder(Order order) {
        ContentValues values = order.toContentValues();
        long insertIndex = database.insert(OrderTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order table.");
        } else {
            Log.d(TAG, "insert order successful.");
        }

        return (int) insertIndex;
    }

    public void setOrderServed(int orderId, boolean served){
        ContentValues values = new ContentValues();
        // 1 is served and 0 don't serve.
        int servedInteger = 0;
        if(served){
            servedInteger = 1;
        }
        values.put(OrderTable.Columns._SERVED, servedInteger);
        String[] whereArgs = {String.valueOf(orderId)};

        int affected = database.update(OrderTable.TABLE_NAME, values, "id=?", whereArgs);
        if (affected == 0) {
            Log.d(TAG, "[Order]update served value in order id " + orderId + " not successful.");
        }
    }

    public void addOrderItem(OrderItem orderItem) {
        ContentValues values = orderItem.toContentValues();
        long insertIndex = database.insert(OrderItemTable.TABLE_NAME, null, values);
        if (insertIndex == -1) {
            Log.d(TAG, "An error occurred on inserting order_item table.");
        } else {
            Log.d(TAG, "insert order_item successful.");
        }
    }

    public ArrayList<Order> getAllOrder() {
        ArrayList<Order> orders = new ArrayList<Order>();
        String sql = "SELECT * FROM 'order'";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            Order order = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                order = order.newInstance(cursor);
                orders.add(order);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of order: " + orders.size());

        return orders;
    }

    public ArrayList<Order> getAllOrderServed() {
        ArrayList<Order> orders = new ArrayList<Order>();
        String sql = "SELECT * FROM 'order' WHERE served=1 ORDER BY order_time DESC";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            Order order = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                order = order.newInstance(cursor);
                orders.add(order);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of order: " + orders.size());

        return orders;
    }

    public ArrayList<Order> getAllOrderNotServed() {
        ArrayList<Order> orders = new ArrayList<Order>();
        String sql = "SELECT * FROM 'order' WHERE served=0 ORDER BY order_time DESC";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            Order order = null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                order = order.newInstance(cursor);
                orders.add(order);
                cursor.moveToNext();
            }
        }
        cursor.close();

        Log.d(TAG, "Number of order: " + orders.size());

        return orders;
    }

    public ArrayList<OrderDetailItem> getOrderDetail(int orderId) {
        ArrayList<OrderDetailItem> orderDetailItems = new ArrayList<OrderDetailItem>();
        String sql = "SELECT menu_id, name_th, price, amount, option" +
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
                orderDetailItem.setOption(cursor.getString(4));
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
                "WHERE order_time like '" + day +"%' AND served=1";
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            income = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
        }
        cursor.close();

        return income;
    }

}
