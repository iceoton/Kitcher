package com.itonlab.kitcher.model;


import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

public class Picture {
    private int id;
    private Bitmap picture;

    public static Picture newInstance(Cursor cursor) {
        Picture picture = new Picture();
        picture.fromCursor(cursor);

        return picture;
    }

    public void fromCursor(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow(PictureTable.Columns._ID));
        byte[] blobPicture = cursor.getBlob(cursor.getColumnIndexOrThrow(PictureTable.Columns._PICTURE));
        this.picture = BitmapFactory.decodeByteArray(blobPicture, 0, blobPicture.length);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(PictureTable.Columns._PICTURE, getByteArrayPicture());

        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getBitmapPicture() {
        return picture;
    }

    public byte[] getByteArrayPicture() {
        // convert the images as byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }


    public void setPicture(Drawable drawable) {
        this.picture = ((BitmapDrawable) drawable).getBitmap();
    }

}
