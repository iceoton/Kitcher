package com.itonlab.kitcher.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileManager {
    private Context mContext;
    private String internalPath;
    private static final String TAG = "FileManager";
    public static final String DATABASE_SUBDIR = "/databases/";

    public FileManager(Context context) {
        this.mContext = context;
        internalPath = context.getFilesDir().getParent();
    }

    public boolean isExistInInternal(String subDir, String fileName) {
        boolean isExist = false;
        String filePath = internalPath + subDir + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            isExist = true;
            Log.d(TAG, "Existing file " + fileName + " in app.");
            Log.d(TAG, "file length = " + file.length());
        } else {
            isExist = false;
            Log.d(TAG, "No existing file, file path: " + filePath);
        }

        return isExist;
    }

    public void deleteFileFromInternal(String subDir, String fileName) {
        if (isExistInInternal(subDir, fileName)) {
            String filePath = internalPath + subDir + fileName;
            File file = new File(filePath);
            if (!file.delete()) {
                Log.d(TAG, "Unable to delete file: " + filePath);
            }
        }
    }

    public void copyAssetsFileToInternal(String fileName, String subDir) {
        File subDirFolder = new File(internalPath + subDir);
        if (!subDirFolder.exists()) {
            subDirFolder.mkdir();
        }
        InputStream input;
        OutputStream output;
        String outFilePath = internalPath + subDir + fileName;
        try {
            input = mContext.getAssets().open(fileName);
            output = new FileOutputStream(outFilePath);
            byte[] buf = new byte[2048];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            input.close();
            output.flush();
            output.close();
            Log.d(TAG, "Copy file to internal storage successfully.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public Drawable getDrawableFromAsset(String assetPath)
    {
        AssetManager assetManager = mContext.getAssets();
        // To load image
        InputStream inputStream = null;
        try {
            // get input stream
            inputStream = assetManager.open(assetPath);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        // create drawable from stream
        Drawable drawable = Drawable.createFromStream(inputStream, null);

        return drawable;
    }

}
