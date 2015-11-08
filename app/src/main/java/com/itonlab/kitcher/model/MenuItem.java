package com.itonlab.kitcher.model;

import android.database.Cursor;


/**
 * This class is item show in food menu.
 */
public class MenuItem {
    private int id;
    private String code;
    private String nameThai;
    private String nameEng;
    private double price;
    private Picture picture;

    public static MenuItem newInstance(Cursor cursor) {
        MenuItem menuItem = new MenuItem();
        menuItem.fromCursor(cursor);

        return menuItem;
    }

    public void fromCursor(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow(MenuTable.Columns._ID));
        this.code = cursor.getString(cursor.getColumnIndexOrThrow(MenuTable.Columns._CODE));
        this.nameThai = cursor.getString(cursor.getColumnIndexOrThrow(MenuTable.Columns._NAME_THAI));
        this.nameEng = cursor.getString(cursor.getColumnIndexOrThrow(MenuTable.Columns._NAME_ENG));
        this.price = cursor.getDouble(cursor.getColumnIndexOrThrow(MenuTable.Columns._PRICE));

        int pictureId = cursor.getInt(cursor.getColumnIndexOrThrow(MenuTable.Columns._PICTURE_ID));
        byte[] blobPicture = cursor.getBlob(cursor.getColumnIndexOrThrow(PictureTable.Columns._PICTURE));
        picture = Picture.newInstance(pictureId, blobPicture);
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setNameThai(String nameThai) {
        this.nameThai = nameThai;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
