package com.itonlab.kitcher.model;

import android.database.Cursor;

public class FoodItem {
    private int id;
    private String code;
    private String nameThai;
    private String nameEng;
    private double price;
    private String imgPath;

    public static FoodItem newInstance(Cursor cursor){
        FoodItem foodItem = new FoodItem();
        foodItem.fromCursor(cursor);

        return  foodItem;
    }

    public void fromCursor(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow(MenuTable.Columns._ID));
        this.code = cursor.getString(cursor.getColumnIndexOrThrow(MenuTable.Columns._CODE));
        this.nameThai = cursor.getString(cursor.getColumnIndexOrThrow(MenuTable.Columns._NAME_THAI));
        this.nameEng = cursor.getString(cursor.getColumnIndexOrThrow(MenuTable.Columns._NAME_ENG));
        this.price = cursor.getDouble(cursor.getColumnIndexOrThrow(MenuTable.Columns._PRICE));
        this.imgPath = cursor.getString(cursor.getColumnIndexOrThrow(MenuTable.Columns._IMAGE_PATH));
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getNameThai() {
        return nameThai;
    }

    public String getNameEng() {
        return nameEng;
    }

    public double getPrice() {
        return price;
    }

    public String getImgPath() {
        return imgPath;
    }
}
