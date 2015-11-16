package com.itonlab.kitcher.model;

import android.content.ContentValues;

public class OrderItem {
    private int id;
    private int orderID;
    private String menuCode;
    private int quantity;
    private String option;
    private boolean served = false;

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(OrderItemTable.Columns._ORDER_ID,this.orderID);
        values.put(OrderItemTable.Columns._MENU_CODE, this.menuCode);
        values.put(OrderItemTable.Columns._QUANTITY, this.quantity);
        values.put(OrderItemTable.Columns._OPTION, this.option);
        int servedValue = 0;
        if(served){
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

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
