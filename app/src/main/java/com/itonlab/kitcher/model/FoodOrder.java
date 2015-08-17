package com.itonlab.kitcher.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

public class FoodOrder {
    private int id;
    private String code;
    private String customerName;
    private String customerIP;
    private int amount;
    private Date orderTime;

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
        this.amount = cursor.getInt(cursor.getColumnIndexOrThrow(OrderTable.Columns._AMOUNT));
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(OrderTable.Columns._CUSTOMER_NAME, this.customerName);
        values.put(OrderTable.Columns._CUSTOMER_IP,this.customerIP);
        values.put(OrderTable.Columns._AMOUNT, this.amount);
        values.put(OrderTable.Columns._ORDER_TIME, this.orderTime.toString());

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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }
}
