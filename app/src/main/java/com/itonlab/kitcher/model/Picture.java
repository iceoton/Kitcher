package com.itonlab.kitcher.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;

public class Picture {
    private int id;
    private Bitmap picture;

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
        return picture;
    }

    public void setPicture(byte[] blobImage) {
        ByteArrayInputStream imageStream = new ByteArrayInputStream(blobImage);
        this.picture = BitmapFactory.decodeStream(imageStream);
    }


}
