package com.itonlab.kitcher.model;

import android.content.ContentValues;
import android.database.Cursor;

public class OrderItem {
    private int id;
    private int preId;
    private int orderID;
    private String menuCode;
    private int quantity;
    private String option;
    private boolean served = false;
    private Status status = Status.UNDONE;


    public enum Status {
        UNDONE(0),
        DONE(1);


        private int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(OrderItemTable.Columns._PRE_ID, this.preId);
        values.put(OrderItemTable.Columns._ORDER_ID, this.orderID);
        values.put(OrderItemTable.Columns._MENU_CODE, this.menuCode);
        values.put(OrderItemTable.Columns._QUANTITY, this.quantity);
        values.put(OrderItemTable.Columns._OPTION, this.option);
        int servedValue = 0;
        if (served) {
            servedValue = 1;
        }
        values.put(OrderItemTable.Columns._SERVED, servedValue);
        values.put(OrderItemTable.Columns._STATUS, status.getValue());

        return values;
    }

    public static OrderItem newInstance(Cursor cursor) {
        OrderItem orderItem = new OrderItem();
        orderItem.fromCursor(cursor);
        return orderItem;
    }

    public void fromCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._ID));
        this.preId = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._PRE_ID));
        this.orderID = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._ORDER_ID));
        this.menuCode = cursor.getString(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._MENU_CODE));
        this.quantity = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._QUANTITY));
        this.option = cursor.getString(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._OPTION));
        int servedValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._SERVED));
        this.served = servedValue == 1;
        int statusValue = cursor.getInt(cursor.getColumnIndexOrThrow(OrderItemTable.Columns._STATUS));
        this.status = (statusValue == 1) ? Status.DONE : Status.UNDONE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPreId() {
        return preId;
    }

    public void setPreId(int preId) {
        this.preId = preId;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
