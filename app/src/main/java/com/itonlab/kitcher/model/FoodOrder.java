package com.itonlab.kitcher.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FoodOrder {
    private int id;
    private String code;
    private String customerName;
    private String customerIP;
    private int total;
    private double totalPrice;
    private Date orderTime;
    private boolean served = false;

    public static FoodOrder newInstance(Cursor cursor){
        FoodOrder foodOrder = new FoodOrder();
        foodOrder.fromCursor(cursor);

        return  foodOrder;
    }

    public void fromCursor(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._ID));
        this.code = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._CODE));
        this.customerName = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._CUSTOMER_NAME));
        this.customerIP = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._CUSTOMER_IP));
        this.total = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._TOTAL));
        this.totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderTable.Columns._TOTAL_PRICE));
        String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._ORDER_TIME));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            this.orderTime = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int servedValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._SERVED));
        // 1 is served and 0 don't serve.
        if(servedValue == 0){
            this.served = false;
        } else {
            this.served = true;
        }

    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(OrderTable.Columns._CUSTOMER_NAME, this.customerName);
        values.put(OrderTable.Columns._CUSTOMER_IP,this.customerIP);
        values.put(OrderTable.Columns._TOTAL, this.total);
        values.put(OrderTable.Columns._TOTAL_PRICE, this.totalPrice);
        int servedValue = 0;
        if(served){
            servedValue = 1;
        }
        values.put(OrderTable.Columns._SERVED, servedValue);

        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerIP() {
        return customerIP;
    }

    public void setCustomerIP(String customerIP) {
        this.customerIP = customerIP;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }
}
