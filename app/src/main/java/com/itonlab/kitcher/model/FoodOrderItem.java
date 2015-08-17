package com.itonlab.kitcher.model;

import android.content.ContentValues;

public class FoodOrderItem {
    private int id;
    private int orderID;
    private int menuID;
    private int amount;
    private boolean served;

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(OrderItemTable.Columns._ORDER_ID,this.orderID);
        values.put(OrderItemTable.Columns._MENU_ID, this.menuID);
        values.put(OrderItemTable.Columns._AMOUNT, this.amount);
        int servedValue = 0;
        if(served == true){
            servedValue = 1;
        }
        values.put(OrderItemTable.Columns._SERVED, servedValue);

        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getMenuID() {
        return menuID;
    }

    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }
}
