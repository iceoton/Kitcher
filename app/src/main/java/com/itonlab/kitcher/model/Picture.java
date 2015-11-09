package com.itonlab.kitcher.model;


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
