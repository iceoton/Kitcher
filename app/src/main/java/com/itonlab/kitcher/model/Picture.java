package com.itonlab.kitcher.model;


import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Picture {
    private int id;
    private byte[] picture;

    public static Picture newInstance(int id, byte[] blobImage) {
        Picture picture = new Picture();
        picture.setId(id);
        picture.setPicture(blobImage);

        return picture;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(PictureTable.Columns._PICTURE, this.picture);

        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getBitmapPicture() {
        return BitmapFactory.decodeByteArray(picture, 0, picture.length);
    }


    public void setPicture(byte[] blobImage) {
        this.picture = blobImage;
    }
}
