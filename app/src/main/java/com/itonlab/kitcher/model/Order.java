package com.itonlab.kitcher.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Order {
    private int id;
    private String code;
    private String customerName;
    private String customerIP;
    private int totalQuantity;
    private double totalPrice;
    private Date orderTime;
    private boolean served = false;
    private Take take = Take.HERE;

    public enum Take {
        HERE(0),
        HOME(1);

        private final int value;

        Take(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static Order newInstance(Cursor cursor) {
        Order order = new Order();
        order.fromCursor(cursor);

        return order;
    }

    public void fromCursor(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._ID));
        this.code = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._CODE));
        this.customerName = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._CUSTOMER_NAME));
        this.customerIP = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._CUSTOMER_IP));
        this.totalQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._TOTAL_QUANTITY));
        this.totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(OrderTable.Columns._TOTAL_PRICE));
        String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(OrderTable.Columns._ORDER_TIME));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        try {
            this.orderTime = dateFormat.parse(dateTime);
            Log.d("DEBUG", "Order Date: " + orderTime.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int servedValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._SERVED));
        // 1 is served and 0 don't serve.
        this.served = servedValue == 1;

        int takeValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._TAKE));
        setTakeByValue(takeValue);

    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(OrderTable.Columns._CUSTOMER_NAME, this.customerName);
        values.put(OrderTable.Columns._CUSTOMER_IP,this.customerIP);
        values.put(OrderTable.Columns._TOTAL_QUANTITY, this.totalQuantity);
        values.put(OrderTable.Columns._TOTAL_PRICE, this.totalPrice);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String nowTime = dateFormat.format(new Date());
        values.put(OrderTable.Columns._ORDER_TIME, nowTime);
        int servedValue = 0;
        if(served){
            servedValue = 1;
        }
        values.put(OrderTable.Columns._SERVED, servedValue);
        values.put(OrderTable.Columns._TAKE, take.getValue());

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

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
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

    public Take getTake() {
        return take;
    }

    public void setTake(Take take) {
        this.take = take;
    }

    public void setTakeByValue(int takeValue) {
        switch (takeValue) {
            case 1:
                this.take = Take.HOME;
                break;
            default:
                this.take = Take.HERE;
        }
    }
}
